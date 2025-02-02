package com.example.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
// Use Lombok to inject Slf4J logger
import lombok.extern.slf4j.Slf4j;

// Add imports
import org.springframework.cloud.gcp.pubsub.core.*;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import java.io.*;
import org.springframework.http.*;
@Controller
@SessionAttributes("name")
// Add Slf4j
@Slf4j

public class FrontendController {
	    // The ApplicationContext is needed to create a new Resource.
	@Autowired
	private ApplicationContext context;
		// Get the Project ID, as its Cloud Storage bucket name here
	@Autowired
	private GcpProjectIdProvider projectIdProvider;
	@Autowired
	private GuestbookMessagesClient client;
	
	@Value("${greeting:Hello}")
	private String greeting;

    @Autowired
	private OutboundGateway outboundGateway;
	
	@GetMapping("/")
	public String index(Model model) {
		if (model.containsAttribute("name")) {
			String name = (String) model.asMap().get("name");
			model.addAttribute("greeting", String.format("%s %s", greeting, name));
		}
		model.addAttribute("messages", client.getMessages().getContent());
		return "index";
	}
	
	@PostMapping("/post")
	public String post(
		@RequestParam(name="file", required=false) MultipartFile file,
		  @RequestParam String name,
		  @RequestParam String message, Model model)
		throws IOException {
		model.addAttribute("name", name);
		String filename = null;
        if (file != null && !file.isEmpty()
            && file.getContentType().equals("image/jpeg")) {
                // Bucket ID is our Project ID
                String bucket = "gs://" +
                      projectIdProvider.getProjectId();
                // Generate a random file name
                filename = UUID.randomUUID().toString() + ".jpg";
                WritableResource resource = (WritableResource)
                      context.getResource(bucket + "/" + filename);
                // Write the file to Cloud Storage
                try (OutputStream os = resource.getOutputStream()) {
                     os.write(file.getBytes());
                 }
        }
		if (message != null && !message.trim().isEmpty()) {
            // Add a log message at the beginning
			log.info("Saving message");

			// Post the message to the backend service
			GuestbookMessage payload = new GuestbookMessage();
			payload.setName(name);
			payload.setMessage(message);
			payload.setImageUri(filename);
			client.add(payload);

            // Add a log message at the end.
			log.info("Saved message");


            // At the very end, publish the message
            outboundGateway.publishMessage(name + ": " + message);
		}
		return "redirect:/";
  }
}

