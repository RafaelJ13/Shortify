package dev.rafaelj13.shortify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import dev.rafaelj13.shortify.dto.*;
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
    
    @Operation(summary = "Listar todos os links", description = "Retorna todos os links encurtados do banco de dados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de links retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao buscar links")
    })
    @GetMapping(value = "/links", produces = "application/json")
    public String getLinks() {
        try {
            var rs = linkDAO.test();
            StringBuilder result = new StringBuilder("[");
            boolean first = true;
            int count = 0;
            while (rs.next()) {
                if (!first) result.append(",");
                result.append("\n  {")
                      .append("\n    \"id\": ").append(rs.getInt("id")).append(",")
                      .append("\n    \"original_link\": \"").append(rs.getString("original_link")).append("\",")
                      .append("\n    \"clicks\": ").append(rs.getInt("clicks")).append(",")
                      .append("\n    \"created_at\": \"").append(rs.getTimestamp("created_at")).append("\"")
                      .append("\n  }");
                first = false;
                count++;
            }
            result.append("\n]");
            System.out.println("Found " + count + " links");
            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
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
    @PostMapping(value = "/shorten", consumes = "application/json", produces = "application/json")
    public Object createLink(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "JSON com a URL para encurtar",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        value = "{\"url\": \"https://www.google.com\"}"
                    )
                )
            )
            @RequestBody LinkRequest request) {
        try {
            System.out.println("Received link: " + request.getUrl());
            Link newLink = new Link(request.getUrl());
            int generatedId = linkDAO.addLinkDB(newLink);
            String shortCode = LinkService.encodeBase62(generatedId);
            System.out.println("Link inserted with ID: " + generatedId);
            return new LinkResponse("http://127.0.0.1:8080/api/" + shortCode, generatedId);
        } catch (SQLException e) {
            System.err.println("Error inserting link: " + e.getMessage());
            e.printStackTrace();
            return new ErrorResponse(e.getMessage());
        }
    }
    
    @Operation(summary = "Deletar link", description = "Marca um link como deletado (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Link deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Link não encontrado ou já deletado")
    })
    @PostMapping(value = "/delete", consumes = "application/json", produces = "application/json")
    public Object deleteLink(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "JSON com o código do link",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        value = "{\"url\": \"3d7\"}"
                    )
                )
            )
            @RequestBody LinkRequest request) throws SQLException {
        int decodedId = LinkService.decodeBase62(request.getUrl());
        Link link = linkDAO.getLinkById(decodedId);
        if (link != null && !link.isDeleted()) {
            linkDAO.deleteLink(link.getId());
            return new StatusResponse("success", "Link deleted successfully");
        } else {
            return new ErrorResponse("Link not found or already deleted");
        }
    }

}