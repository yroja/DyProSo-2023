package DyProSo2023.files;

import DyProSo2023.User.Attendee;
import DyProSo2023.User.AttendeeManagement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Controller responsible for handling file-related operations.
 * 
 * Provides endpoints for uploading, downloading, filtering and managing documents.
 */

@Controller
public class FileController {
    private final DocManagement docManagement;
    private final AttendeeManagement attendeeManagement;

    public FileController(DocManagement docManagement, AttendeeManagement attendeeManagement){
        this.docManagement = docManagement;
        this.attendeeManagement = attendeeManagement;
    }

    @GetMapping("/upload")
    public String getUploadPage(@ModelAttribute("docForm") DocForm docForm, Model model){
        model.addAttribute("title", "Upload");
        if(!model.containsAttribute("successCategories")){
            model.addAttribute("successCategories", true);
        }
        if(!model.containsAttribute("exceededCategories")){
            model.addAttribute("exceededCategories", false);
        }
        if(!model.containsAttribute("successPoster")){
            model.addAttribute("successPoster", true);
        }
        if (docManagement.getAbstractSubmissionIsActive()){
            model.addAttribute("msg", null);
            return "upload";
        }

        if(!model.containsAttribute("maxAmountOfFiles")){
            model.addAttribute("maxAmountOfFiles", false);
        }
        if(!model.containsAttribute("maxFileSize")){
            model.addAttribute("maxFileSize", false);
        }
        if(!model.containsAttribute("successUpload")){
            model.addAttribute("successUpload", false);
        }
        if(!model.containsAttribute("uploadedFile")){
            model.addAttribute("uploadedFile", null);
        }

        return "redirect:/abstract";
    }


    /**
     * Handles file upload for posters and abstracts.
     * 
     * Validates user constraints (max file count, categories, poster flag), stores the file, 
     * updates attendee state and sends notification mail.
     * 
     * @param docForm form data containing metadata
     * @param file uploaded file
     */
    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute("docForm") DocForm docForm, @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee author = attendeeManagement.getAttendeeByName(auth.getName());

        if(author.getFilesUploaded() == 3){
            redirectAttributes.addFlashAttribute("maxAmountOfFiles", true);
            return "redirect:/upload";
        }

        if(docForm.getPoster() == null){
            redirectAttributes.addFlashAttribute("successPoster", false);
            return "redirect:/upload";
        }

        if(docForm.getCategories() == null || docForm.getCategories().length == 1 && docForm.getCategories()[0].equals("")){
            redirectAttributes.addFlashAttribute("successCategories", false);
            return "redirect:/upload";
        }

        if(docForm.getCategories().length > 3){
            redirectAttributes.addFlashAttribute("exceededCategories", true);
            return "redirect:/upload";
        }

        docManagement.saveFile(file, author, docForm.getCategories(), docForm.getPoster());
        attendeeManagement.uploadFile(author);
        try{
            attendeeManagement.send_upload_complete_mail(author);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("successUpload", true);
        redirectAttributes.addFlashAttribute("uploadedFile", file.getOriginalFilename());

        return "redirect:/upload";
    }

    /**
     * Uploads the conference schedule (admin only).
     * 
     * Replaces existing schedule file if present.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/post_schedule")
    public String upload_dyproso_schedule(@RequestParam("file") MultipartFile file, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee author = attendeeManagement.getAttendeeByName(auth.getName());
        Doc schedule_doc = null;
        for(Doc doc : docManagement.getAllDocs()){
            if(doc.getAuthor() == null){
                schedule_doc = doc;
                break;
            }
        }
        if(schedule_doc != null){
            docManagement.deleteDoc(schedule_doc);
        }
        docManagement.saveFile(file, author,null, false);
        return "redirect:/timetable";
    }

    /**
     * Deletes the current schedule file if it exists.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/delete_schedule")
    public String delete_dyproso_schedule(Model model){
        Doc dyproso_doc = null;
        for(Doc doc : docManagement.getAllDocs()){
            if(doc.getAuthor() == null){
                dyproso_doc = doc;
                break;
            }
        }
        if(dyproso_doc != null){
            docManagement.deleteDoc(dyproso_doc);
        }

        return "redirect:/timetable";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer fileId){
        Doc doc = docManagement.getFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\""+doc.toString()+ "\"")
                .body(new ByteArrayResource(doc.getData()));
    }

    @GetMapping("/download_schedule/{fileId}")
    public ResponseEntity<ByteArrayResource> download_schedule(@PathVariable Integer fileId){
        Doc doc = docManagement.getFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=DyProSo-2023 schedule")
                .body(new ByteArrayResource(doc.getData()));
    }

    /**
     * Creates and downloads a ZIP archive containing all uploaded documents.
     * 
     * The method collects all documents, writes them to a temporary directory, compresses them into a ZIP file
     * and returns it as a download response.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/download_bundled_abstracts")
    public ResponseEntity<ByteArrayResource> download_bundled_abstracts() throws IOException {
        List<Doc> docs = new ArrayList<Doc>();
        for(Doc doc : docManagement.getAllDocs()){
            if(doc.getAuthor() == null){
                continue;
            }
            docs.add(doc);
        }

        Path tempDir = Files.createTempDirectory("temp_dir");
        for (Doc doc : docs) {
            Path filePath = tempDir.resolve(doc.getFileName());
            Files.write(filePath, doc.getData());
        }
        String zipFileName = "abstracts.zip";
        Path zipFilePath = tempDir.resolve(zipFileName);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
        for (File file : tempDir.toFile().listFiles()) {
            if(file.getName().equals(zipFileName)){
                continue;
            }
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            Files.copy(file.toPath(), zipOutputStream);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();

        // Create a ByteArrayResource with the zip file data
        byte[] zipBytes = Files.readAllBytes(zipFilePath);
        ByteArrayResource resource = new ByteArrayResource(zipBytes);

        // Set the response headers for the download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            if (Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
        // Return the zip file as a ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipBytes.length)
                .body(resource);
        }

    /**
     * Deletes a document and updates attendee file count.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/delete")
    public String delete(@RequestParam("fileId") Integer fileId){
        Doc doc = docManagement.getFileById(fileId);
        attendeeManagement.deleteFile(doc.getAuthor());
        docManagement.deleteDoc(doc);
        return "redirect:/admin/documents";
    }
}
