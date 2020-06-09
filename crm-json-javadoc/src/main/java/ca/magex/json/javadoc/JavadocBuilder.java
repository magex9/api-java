package ca.magex.json.javadoc;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

/**
 * Iterate over the classes and print their Javadoc in JSON format.
 */
public class JavadocBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JavadocBuilder.class);
	
    public static JsonObject processDirectory(File src) throws Exception {
    	logger.info("Processing root: " + src.getAbsolutePath());

    	List<JsonPair> pairs = new ArrayList<JsonPair>();
    	for (String filename : findFiles(src)) {
    		String clsName = filename.replaceAll(".java$", "").replaceAll("/", ".");
    		logger.info("Building: " + clsName);
        	File file = new File(src, filename);
    		pairs.add(new JsonPair(clsName, processClass(file)));
    	}
    	
    	return new JsonObject(pairs);
    }
    
    public static File processDirectory(File src, File output) throws Exception {
    	logger.info("Creating file: " + output.getAbsolutePath());
    	FileUtils.writeStringToFile(output, processDirectory(src).toString(), StandardCharsets.UTF_8);
    	return output;
    }

    public static List<String> findFiles(File src) {
    	return findFiles(src, "");
    }
    
    private static List<String> findFiles(File src, String path) {
    	List<String> files = Arrays.asList(new File(src, path).listFiles()).stream()
    		.filter(f -> f.isFile())
    		.map(f -> path + f.getName())
    		.collect(Collectors.toList());
    	
    	Arrays.asList(new File(src, path).listFiles()).stream()
    		.filter(f -> f.isDirectory())
    		.forEach(f -> files.addAll(findFiles(src, path + f.getName() + "/")));
    	
    	return files;
    }
    
    public static JsonObject processClass(File file) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(file);

        JsonObject json = new JsonObject()
        	.with("description", buildComment(cu.findAll(ClassOrInterfaceDeclaration.class).get(0).getComment()))
        	.with("imports", buildImports(cu))
        	.with("fields", new JsonArray())
        	.with("methods", new JsonArray());
        
        for (TypeDeclaration<?> typeDec : cu.getTypes()) {
            List<BodyDeclaration<?>> members = typeDec.getMembers();
            if (members != null) {
                for (BodyDeclaration<?> member : members) {
                	if (member.isFieldDeclaration()) {
                		for (int i = 0; i < ((FieldDeclaration)member).getVariables().size(); i++) {
                			json = json.append("fields", buildField((FieldDeclaration)member, i));
                		}
                	}
                    if (member.isMethodDeclaration()) {
                        json = json.append("methods", buildMethod((MethodDeclaration)member));    
                    }
                }
            }
        }
        
        return json;
    }
    
    private static String buildComment(Optional<Comment> comment) {
    	if (!comment.isPresent())
    		return null;
    	StringBuilder sb = new StringBuilder();
    	for (String line : comment.get().toString().split("\n")) {
    		sb.append("\n");
    		sb.append(line
    			.replaceAll("^\\s*/?\\*+/?\\s*", "")
    			.replaceAll("^\\s*//\\s*", ""));
    	}
    	return sb.substring(1).trim();
    }
    
    private static JsonObject buildImports(CompilationUnit cu) {
		return new JsonObject(cu.getImports().stream()
   			.map(i -> new JsonPair(
   				i.getNameAsString().substring(i.getNameAsString().lastIndexOf('.') + 1), 
   				i.getNameAsString().substring(0, i.getNameAsString().lastIndexOf('.'))))
   			.collect(Collectors.toList()));
	}

	private static JsonObject buildField(FieldDeclaration field, int index) {
		if (field.getVariable(index).getNameAsString().equals("serialVersionUID")) 
			return null;
    	JsonObject json = new JsonObject();
    	if (!field.getVariables().isEmpty()) {
   			json = json.with("name", field.getVariable(index).getNameAsString()); 
    	}
    	if (field.getComment().isPresent()) {
    		json = json.with("description", buildComment(field.getComment()));
    	}
    	json = json.with("modifiers", new JsonArray(field.getModifiers().stream()
    		.map(n -> new JsonText(n.getKeyword().toString().toLowerCase()))
    		.collect(Collectors.toList())));
    	if (!field.getVariable(index).getType().getChildNodes().isEmpty()) {
    		json = json.with("type", buildType(field.getVariable(index).getType()));
    	}
    	if (!field.getAnnotations().isEmpty()) {
    		json = json.with("annotations", field.getAnnotations().stream()
    	    	.map(a -> buildAnnotation(a)).collect(Collectors.toList()));
    	}
    	return json;
    }
    
    private static JsonElement buildType(Type type) {
    	if (type instanceof PrimitiveType) {
        	return new JsonText(((PrimitiveType)type).getElementType().toString());
    	}
    	if (type.getChildNodes().size() > 1) {
        	JsonObject json = new JsonObject();
        	json = json.with("class", type.getChildNodes().get(0).toString());
    		json = json.with("generics", type.getChildNodes().subList(1, type.getChildNodes().size())
    			.stream().map(n -> n.toString()).collect(Collectors.toList()));
        	return json;
    	} else {
    		return new JsonText(type.getChildNodes().get(0).toString());
    	}
    }
    
    private static JsonObject buildMethod(MethodDeclaration method) {
    	JsonObject json = new JsonObject();
    	json = json.with("name", method.getNameAsString());
   		json = json.with("description", buildComment(method.getComment()));
    	json = json.with("modifiers", new JsonArray(method.getModifiers().stream()
    		.map(n -> new JsonText(n.getKeyword().toString().toLowerCase()))
    		.collect(Collectors.toList())));
    	if (!method.getType().getChildNodes().isEmpty()) {
    		json = json.with("type", buildType(method.getType()));
    	}
    	if (!method.getAnnotations().isEmpty()) {
    		json = json.with("annotations", method.getAnnotations().stream()
    	    	.map(a -> buildAnnotation(a)).collect(Collectors.toList()));
    	}
    	if (!method.getParameters().isEmpty()) {
	    	json = json.with("parameters", method.getParameters().stream()
	    		.map(p -> buildParameter(p)).collect(Collectors.toList()));
    	}
    	if (method.getBody().isPresent()) {
    		json = json.with("body", method.getBody().get().toString());
    	}
    	return json;
    }
    
    private static JsonObject buildParameter(Parameter parameter) {
    	JsonObject json = new JsonObject();
    	json = json.with("name", parameter.getNameAsString());
    	json = json.with("type", buildType(parameter.getType()));
    	if (!parameter.getAnnotations().isEmpty()) {
    		json = json.with("annotations", parameter.getAnnotations().stream()
    	    	.map(a -> buildAnnotation(a)).collect(Collectors.toList()));
    	}
    	return json;
    }

	private static JsonObject buildAnnotation(AnnotationExpr annotation) {
    	JsonObject json = new JsonObject();
    	json = json.with("name", annotation.getNameAsString());
    	List<MemberValuePair> properties = annotation.findAll(MemberValuePair.class);
    	if (!properties.isEmpty()) {
    		json = json.with("properties", properties.stream().map(p -> buildPair(p)).collect(Collectors.toList()));
    	}
    	annotation.getDataKeys();
		return json;
	}
	
	private static JsonObject buildPair(MemberValuePair pair) {
		return new JsonObject()
			.with("key", pair.getNameAsString())
			.with("value", pair.getValue().toString());
	}

}