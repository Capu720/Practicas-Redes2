import java.util.*;

public class Mime {
		public static HashMap<String, String> mimeTypes;

		public Mime() {
			mimeTypes = new HashMap<>();

			mimeTypes.put("pdf", "application/pdf");
			mimeTypes.put("jpg", "image/jpeg");
			mimeTypes.put("jpeg", "image/jpeg");
			mimeTypes.put("png", "image/png");
			mimeTypes.put("html", "text/html");
			mimeTypes.put("htm", "text/html");
		}

		public String get(String extension) {
			if(mimeTypes.containsKey(extension))
				return mimeTypes.get(extension);
			else
				return "application/octet-stream";
		}

	}