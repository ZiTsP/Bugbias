package libraries.putils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class CodingLanguage {

	private final String language;
	private final List<String> extension;
	private final Optional<String> commentDelimiter;

    protected CodingLanguage(String language, List<String> extension, String commentDelimiter) {
        this.language = language;
        this.extension = extension;
        this.commentDelimiter = Optional.ofNullable(commentDelimiter);
    }
    
    protected CodingLanguage(String language, String extension, String commentDelimiter) {
        this(language, Arrays.asList(extension), commentDelimiter);
    }

	public final String getLanguage() {
		return this.language;
	}

	public final String toString() {
		StringBuffer strings = new StringBuffer();
		strings.append(this.language);
		strings.append(" [");
		for (String entry : this.extension) {
			strings.append(entry);
			strings.append(" , ");
		}
		strings.delete(strings.length() - 3, strings.length() - 1);
		strings.append("]");
		return strings.toString();
	}

	public final List<String> getExtension() {
		return extension;
	}
	
	public final Optional<String> getCommentDelimiter() {
	    return this.commentDelimiter;
	}
}
