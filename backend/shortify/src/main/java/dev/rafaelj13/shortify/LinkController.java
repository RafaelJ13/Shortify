package dev.rafaelj13.shortify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;


@RestController
@RequestMapping("/api")
@Tag(name = "Shortify API", description = "API para encurtar e gerenciar URLs")
public class LinkController {

    @Autowired
    private LinkDAO linkDAO;
    
    @Operation(summary = "Redirecionar para documentação", description = "Redireciona para a página de documentação Swagger")
    @GetMapping
    public void getMethodName(@RequestParam String param) {
        new RedirectView("/api/docs");
    }
    
    @Operation(summary = "Listar todos os links", description = "Retorna todos os links encurtados do banco de dados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de links retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao buscar links")
    })
    @GetMapping("/links")
    public String getLinks() {
        try {
            var rs = linkDAO.test();
            StringBuilder result = new StringBuilder("[");
            boolean first = true;
            int count = 0;
            while (rs.next()) {
                if (!first) result.append(", ");
                result.append("{")
                      .append("\"id\": ").append(rs.getInt("id"))
                      .append(", \"original_link\": \"").append(rs.getString("original_link"))
                      .append("\", \"clicks\": ").append(rs.getInt("clicks"))
                      .append(", \"created_at\": \"").append(rs.getTimestamp("created_at"))
                      .append("\"}");
                first = false;
                count++;
            }
            result.append("]");
            System.out.println("Found " + count + " links");
            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Operation(summary = "Redirecionar para URL original", description = "Redireciona para a URL original usando o código encurtado e incrementa o contador de cliques")
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Redirecionamento bem-sucedido"),
        @ApiResponse(responseCode = "404", description = "Link não encontrado ou deletado")
    })
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginal(
            @Parameter(description = "Código encurtado do link (Base62)", example = "3d7")
            @PathVariable String shortCode) throws SQLException {
        int id = LinkService.decodeBase62(shortCode);
        Link link = linkDAO.getLinkById(id);
        if (link != null && !link.isDeleted()) {
            linkDAO.incrementClicks(id);
            return new RedirectView(link.getOriginal_Link());
        }
        return new RedirectView("/api/error?param=Link not found or deleted");
    }
    
    @Operation(summary = "Encurtar URL", description = "Cria uma URL encurtada a partir de uma URL completa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "URL encurtada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao criar link")
    })
    @PostMapping("/shorten")
    public String createLink(
            @Parameter(description = "URL completa para encurtar", example = "https://www.google.com")
            @RequestBody String link) {
        try {
            System.out.println("Received link: " + link);
            Link newLink = new Link(link);
            int generatedId = linkDAO.addLinkDB(newLink);
            String shortCode = LinkService.encodeBase62(generatedId);
            System.out.println("Link inserted with ID: " + generatedId);
            return "{\"shortUrl\": \"http://127.0.0.1:8080/api/" + shortCode + "\"}";
        } catch (SQLException e) {
            System.err.println("Error inserting link: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @Operation(summary = "Deletar link", description = "Marca um link como deletado (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Link deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Link não encontrado ou já deletado")
    })
    @PostMapping("/delete/{id}")
    public String deleteLink(
            @Parameter(description = "Código encurtado do link a deletar (Base62)", example = "3d7")
            @PathVariable String id) throws SQLException {
        int decodedId = LinkService.decodeBase62(id);
        Link link = linkDAO.getLinkById(decodedId);
        if (link != null && !link.isDeleted()) {
            linkDAO.deleteLink(link.getId());
            return "{\"status\": \"Link deleted successfully\"}";
        } else {
            return "{\"error\": \"Link not found or already deleted\"}";
        }
    }
    
    @Operation(summary = "Página de erro", description = "Exibe mensagens de erro")
    @ApiResponse(responseCode = "200", description = "Mensagem de erro retornada")
    @GetMapping("/error")
    public String error(
            @Parameter(description = "Mensagem de erro") 
            @RequestParam String param) {
        return "Error: " + param;
    }
    
}