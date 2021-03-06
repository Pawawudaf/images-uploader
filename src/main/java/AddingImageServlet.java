import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("imageUpload")
@MultipartConfig
public class AddingImageServlet extends HttpServlet {

    private final ImageService imageService;
    private final AmazonS3Service amazonS3Service = new AmazonS3Service();

    public AddingImageServlet(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws jakarta.servlet.ServletException, IOException {
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws jakarta.servlet.ServletException, IOException {
        imageService.saveImage(new Image(
            req.getParameter("imageName"),
            LocalDateTime.now(),
            amazonS3Service.addToS3(req.getPart("imageFile").getInputStream()),
            req.getPart("imageFile").getInputStream().readAllBytes().length
        ));
        if (resp.getStatus() == 200) {
            req.setAttribute("message", "Image successful uploaded!");
        } else {
            req.setAttribute("message", "There is error during uploading! Please, try again!");
        }
        req.getRequestDispatcher("uploadStatus.jsp").forward(req, resp);
    }
}
