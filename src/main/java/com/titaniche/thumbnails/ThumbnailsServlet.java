package com.titaniche.thumbnails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.Logger;

public class ThumbnailsServlet extends HttpServlet {
	private final Logger LOGGER = Logger.getLogger(ThumbnailsServlet.class.getSimpleName());
	//Default path and content type
	private String filePath = "C:/data/thumbnails";
	private String contentType = "image/png";

	@Override
	public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		String pathInfo = httpRequest.getPathInfo();
		String thumbUid = null;
		if (pathInfo != null && pathInfo.startsWith("/")) {
			thumbUid = pathInfo.substring(1);
		}
		try {
			if (thumbUid != null) {
				File localFile = new File(filePath, thumbUid);
				if (localFile.exists()) {
					LOGGER.info("Cache hit, file exists " + localFile.getAbsolutePath());
					httpResponse.setContentType(contentType);
					httpResponse.setContentLength((int) localFile.length());
					httpResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", thumbUid));
					try (FileInputStream in = new FileInputStream(localFile)) {
						copy(in, httpResponse.getOutputStream());
					}
				} else {
					LOGGER.info("Local file not found " + localFile.getAbsolutePath());
					httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			} else {
				LOGGER.info("Can't read uid of thumbnails");
				httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		int bytesRead;
		byte[] buffer = new byte[4096];
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		out.flush();
	}
}
