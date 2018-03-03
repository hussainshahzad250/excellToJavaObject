package com.excell.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class CodeHelper {
	public static String MAIN_PACKAGE = "com.excell";
	public static String MAIN_PACKAGE_PATH = "src/main/java/com/excell";

	public static void main(String[] args) {
	List<TableData> tableList=new ArrayList<>();
	tableList.add(new TableData("firstName", "String", "", false, false));
	tableList.add(new TableData("lastName", "String", "", false, false));
	tableList.add(new TableData("email", "String", "", true, true));
	createCollection("Test", tableList);
	}
	private static void createCollection(String entityName, List<TableData> fieldsMap) {
	createEntity(MAIN_PACKAGE_PATH + "entity/", entityName, fieldsMap);
	createDao(MAIN_PACKAGE_PATH + "dao/", entityName, fieldsMap);
	createDaoImpl(MAIN_PACKAGE_PATH + "dao/impl/", entityName, fieldsMap);
	createPojo(MAIN_PACKAGE_PATH + "pojos/", entityName, fieldsMap);
	createRequest(MAIN_PACKAGE_PATH + "requests/dto/", entityName);
	createResponse(MAIN_PACKAGE_PATH + "responses/dto/", entityName);
	createConverter(MAIN_PACKAGE_PATH + "utils/", entityName, fieldsMap);
	createService(MAIN_PACKAGE_PATH + "services/", entityName, fieldsMap);
	createServiceImpl(MAIN_PACKAGE_PATH + "services/impl/", entityName, fieldsMap);
	createController(MAIN_PACKAGE_PATH + "controllers/", entityName,fieldsMap);
	addRestMapping(MAIN_PACKAGE_PATH + "constants/", entityName);
	addErrorConstants(MAIN_PACKAGE_PATH + "enums/", entityName, fieldsMap);
	addValidations(MAIN_PACKAGE_PATH + "utils/", entityName, fieldsMap);
	}
	private static void addValidations(String filePath, String fileName, List<TableData> fieldsMap) {
	try {
	FileReader fileReader = new FileReader(filePath + "ValidationUtils.java");
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	StringBuilder builder = new StringBuilder();
	String linePre = null;
	String line;
	while ((line = bufferedReader.readLine()) != null) {
	if (line.trim().endsWith("{") && !builder.toString().contains(fileName)) {
	builder.append("import " + MAIN_PACKAGE + ".request.dto." + fileName + "Request;\n");
	builder.append("import " + MAIN_PACKAGE + ".response.dto." + fileName + "Response;\n");
	}
	if (line.trim().endsWith("}/* finish */")) {
	if (linePre.trim().endsWith("}")) {
	if (!builder.toString().contains(fileName + "s Validations")) {
	builder.append("\n").append("\t/* " + fileName + "s Validations */\n");
	builder.append("\tpublic static void validate" + fileName + "Request(" + fileName
	+ "Request request) throws LavaOssException {\n");
	for (TableData entry : fieldsMap) {
	if (entry.isNotNull()) {
	if (entry.getType().equals("String")) {
	builder.append("\tif(StringUtils.isEmpty(request.get"
	+ getUppercase(entry.getName()).charAt(0)
	+ entry.getName().substring(1, entry.getName().length()) + "())){\n");
	} else if (entry.getType().contains("List") || entry.getType().contains("Set")) {
	builder.append("\tif(CollectionUtils.isEmpty(request.get"
	+ getUppercase(entry.getName()).charAt(0)
	+ entry.getName().substring(1, entry.getName().length()) + "())){\n");
	} else {
	builder.append("\tif(request.get" + getUppercase(entry.getName()).charAt(0)
	+ entry.getName().substring(1, entry.getName().length())
	+ "()==null){\n");
	}
	builder.append("\t\tthrow new LavaOssException(LavaOssResponseCode.EMPTY_"
	+ getUppercase(entry.getName()) + ");\n");
	builder.append("\t}\n");
	}
	}
	builder.append("\t}\n\n");
	builder.append("\tpublic static void validate" + fileName + "Response(" + fileName
	+ "Response request) throws LavaOssException {\n");
	builder.append("\tif(StringUtils.isEmpty(request.get" + fileName + "Id())){\n");
	builder.append("\t\tthrow new LavaOssException(LavaOssResponseCode.EMPTY_"
	+ getUppercase(fileName) + "_ID);\n");
	builder.append("\t}\n");
	for (TableData entry : fieldsMap) {
	builder.append(
	"\tif(StringUtils.isEmpty(request.get" + getUppercase(entry.getName()).charAt(0)
	+ entry.getName().substring(1, entry.getName().length()) + "())){\n");
	builder.append("\t\tthrow new LavaOssException(LavaOssResponseCode.EMPTY_"
	+ getUppercase(entry.getName()) + ");\n");
	builder.append("\t}\n");
	}
	builder.append("\t}\n");
	}
	}
	}
	builder.append(line).append("\n");
	linePre = line;
	}
	bufferedReader.close();
	FileOutputStream out = new FileOutputStream(filePath + "ValidationUtils.java");
	out.write(builder.toString().getBytes());
	out.close();
	} catch (IOException e) {
	e.printStackTrace();
	}
	System.out.println("Validations Created!");
	}

	private static void addErrorConstants(String filePath, String fileName, List<TableData> fieldsMap) {
	try {
	FileReader fileReader = new FileReader(filePath + "LavaOssResponseCode.java");
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	StringBuilder builder = new StringBuilder();
	String linePre = "";
	String line;
	while ((line = bufferedReader.readLine()) != null) {
	if (line.trim().endsWith(";")) {
	if (linePre.trim().endsWith(",")) {
	if (!builder.toString().contains("/*"+fileName + "s*/")) {
	builder.append("\n").append("\t/* " + fileName + "s*/\n");
	builder.append("\tEMPTY_" + (getUppercase(fileName) + "_ID") + "(\""
	+ fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length())
	+ "Id cannot be empty!\",\"EMP_" + (fileName + "_ID").toUpperCase() + "\"),\n");
	for (TableData entry : fieldsMap) {
	String errorCode = getUppercase(entry.getName());
	if(!builder.toString().contains("EMPTY_" + errorCode)) {
	builder.append("\tEMPTY_" + errorCode + "(\"" + entry.getName()
	+ " cannot be empty!\",\"EMP_" + errorCode + "\"),\n");
	}
	if (entry.isUnique()) {
	if(!builder.toString().contains("EXIST_" + errorCode)) {
	builder.append("\tEXIST_" + errorCode + "(\"" + entry.getName()
	+ " is already exist!\",\"EXIST_" + errorCode
	+ "\"),\n");
	}
	}
	}
	builder.append("\t" + getUppercase(fileName) + "_NOT_FOUND(\"" + fileName
	+ " not found!\",\"NF_" + getUppercase(fileName) + "\"),\n");
	builder.append("\tNO_" + getUppercase(fileName) + "S_FOUND(\"No " + fileName
	+ "s added yet!\",\"NO_" + getUppercase(fileName) + "_ADDED\"),\n");
	}
	}
	}
	builder.append(line).append("\n");
	linePre = line;
	}
	bufferedReader.close();
	FileOutputStream out = new FileOutputStream(filePath + "LavaOssResponseCode.java");
	out.write(builder.toString().getBytes());
	out.close();
	} catch (IOException e) {
	e.printStackTrace();
	}
	System.out.println("Exeptions Created");
	}
	private static void addRestMapping(String filePath, String fileName) {
	try {
	FileReader fileReader = new FileReader(filePath + "RestMappingConstants.java");
	BufferedReader bufferedReader = new BufferedReader(fileReader);
	StringBuilder builder = new StringBuilder();
	String linePre = null;
	String line;
	while ((line = bufferedReader.readLine()) != null) {
	if (line.trim().endsWith("}")) {
	if (linePre.trim().endsWith("}")) {
	if (!builder.toString().contains(fileName + "Constants")) {
	builder.append("\tinterface " + fileName + "Constants{\n");
	builder.append("\t\tString BASE = APP_BASE + \"/" + fileName.toLowerCase().charAt(0)
	+ fileName.substring(1, fileName.length()) + "s\";\n");
	builder.append("\t}\n");
	}
	}
	}
	builder.append(line).append("\n");
	linePre = line;
	}
	bufferedReader.close();
	FileOutputStream out = new FileOutputStream(filePath + "RestMappingConstants.java");
	out.write(builder.toString().getBytes());
	out.close();
	} catch (IOException e) {
	e.printStackTrace();
	}
	System.out.println("Constants Created");
	}

	private static void createController(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Controller.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".controllers;\n\n");
	fileWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
	fileWriter.write("import org.springframework.http.MediaType;\n");
	fileWriter.write("import org.springframework.stereotype.Controller;\n");
	fileWriter.write("import org.springframework.web.bind.annotation.RequestBody;\n");
	fileWriter.write("import org.springframework.web.bind.annotation.PathVariable;\n");
	fileWriter.write("import org.springframework.web.bind.annotation.RequestMapping;\n");
	fileWriter.write("import org.springframework.web.bind.annotation.RequestMethod;\n");
	fileWriter.write("import org.springframework.web.bind.annotation.ResponseBody;\n\n");

	fileWriter.write("import " + MAIN_PACKAGE + ".constants.RestMappingConstants;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".enums.LavaOssResponseCode;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".exception.LavaOssException;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".responses.dto.BaseApiResponse;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".utils.CollectionUtils;\n");

	fileWriter.write("import " + MAIN_PACKAGE + ".requests.dto." + fileName + "Request;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".responses.dto." + fileName + "Response;\n");
	fileWriter.write("import java.util.List;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".services." + fileName + "Service;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".utils." + fileName + "Converter;\n\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".utils.ValidationUtils;\n\n");

	fileWriter.write("@Controller\n");
	fileWriter.write("@RequestMapping(value = RestMappingConstants." + fileName + "Constants.BASE)\n");
	fileWriter.write("public class " + fileName + "Controller{\n\n");

	fileWriter.write("\t@Autowired\n");
	String serviceName = fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length())
	+ "Service";
	fileWriter.write("\tprivate " + fileName + "Service " + serviceName + ";\n");

	fileWriter.write(
	"\t@RequestMapping(value = RestMappingConstants.REQUEST, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<" + fileName
	+ "Request> generateRequestJson() throws LavaOssException{\n");
	fileWriter.write("\treturn new BaseApiResponse<" + fileName
	+ "Request>(false, LavaOssResponseCode.SUCCESS.getCode(), " + fileName
	+ "Converter.getSample(),LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<Long> add(");
	fileWriter.write("@RequestBody(required = true) " + fileName + "Request request");
	fileWriter.write(")throws LavaOssException{\n");
	fileWriter.write("\tValidationUtils.validate" + fileName + "Request(request);\n");
	for (TableData tableData : fieldsMap) {
	if (tableData.isUnique()) {
	fileWriter.write("\tif("+serviceName+".existBy"+getMethodeName(tableData.getName())+"(request.get"+getMethodeName(tableData.getName())+"())){\n");
	fileWriter.write("\t\tthrow new LavaOssException(LavaOssResponseCode.EXIST_" + getUppercase(tableData.getName())+");\n");
	fileWriter.write("\t}\n");
	}
	}
	fileWriter.write("\tLong response = " + serviceName + ".add(request);\n");
	fileWriter.write(
	"\treturn new BaseApiResponse<Long>(false, LavaOssResponseCode.SUCCESS.getCode(), response, LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@RequestMapping(path = RestMappingConstants.ID_PARAM, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<" + fileName + "Response> getById(");
	fileWriter.write("@PathVariable(RestMappingConstants.ID) Long id)throws LavaOssException{\n");
	fileWriter.write("\t" + fileName + "Response response = " + serviceName + ".getById(id);\n");
	fileWriter.write("\tif(response==null){\n");
	fileWriter.write(
	"\t\tthrow new LavaOssException(LavaOssResponseCode." + fileName.toUpperCase() + "_NOT_FOUND);\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn new BaseApiResponse<" + fileName
	+ "Response>(false, LavaOssResponseCode.SUCCESS.getCode(), response, LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<List<" + fileName + "Response>> getAll()throws LavaOssException{\n");
	fileWriter.write("\tList<" + fileName + "Response> response = " + serviceName + ".getAll();\n");
	fileWriter.write("\tif(CollectionUtils.isEmpty(response)){\n");
	fileWriter.write(
	"\t\tthrow new LavaOssException(LavaOssResponseCode.NO_" + fileName.toUpperCase() + "S_FOUND);\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn new BaseApiResponse<List<" + fileName
	+ "Response>>(false, LavaOssResponseCode.SUCCESS.getCode(), response, LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@RequestMapping(path = RestMappingConstants.ID_PARAM, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<Boolean> delete(");
	fileWriter.write("@PathVariable(RestMappingConstants.ID) Long id)throws LavaOssException{\n");
	fileWriter.write("\tif(!" + serviceName + ".exist(id)){\n");
	fileWriter.write(
	"\t\tthrow new LavaOssException(LavaOssResponseCode." + fileName.toUpperCase() + "_NOT_FOUND);\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\t" + serviceName + ".delete(id);\n");
	fileWriter.write(
	"\treturn new BaseApiResponse<Boolean>(false, LavaOssResponseCode.SUCCESS.getCode(), true, LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@RequestMapping(method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)\n");
	fileWriter.write("\t@ResponseBody\n");
	fileWriter.write("\tpublic BaseApiResponse<Boolean> update(");
	fileWriter.write("@RequestBody(required = true) " + fileName + "Response request)throws LavaOssException{\n");
	fileWriter.write("\tif(!" + serviceName + ".exist(request.get" + fileName + "Id())){\n");
	fileWriter.write(
	"\t\tthrow new LavaOssException(LavaOssResponseCode." + fileName.toUpperCase() + "_NOT_FOUND);\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\tBoolean response = " + serviceName + ".update(request);\n");
	fileWriter.write(
	"\treturn new BaseApiResponse<Boolean>(false, LavaOssResponseCode.SUCCESS.getCode(), response, LavaOssResponseCode.SUCCESS.getMessage());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Controller Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createServiceImpl(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "ServiceImpl.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".services.impl;\n\n");
	fileWriter.write("import org.springframework.beans.factory.annotation.Autowired;\n");
	fileWriter.write("import org.springframework.stereotype.Service;\n\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".requests.dto." + fileName + "Request;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".responses.dto." + fileName + "Response;\n");
	fileWriter.write("import java.util.List;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".services." + fileName + "Service;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".dao." + fileName + "Dao;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".utils." + fileName + "Converter;\n\n");
	fileWriter.write("@Service\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("ServiceImpl implements " + fileName + "Service{\n\n");
	fileWriter.write("\t@Autowired\n");
	String daoName = fileName.toLowerCase().charAt(0) + fileName.substring(1, fileName.length()) + "Dao";
	fileWriter.write("\tprivate " + fileName + "Dao " + daoName + ";\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic Long add(" + fileName + "Request request){\n");
	fileWriter.write("\t" + fileName + " entity=" + fileName + "Converter.getEntityFromRequest(request);\n");
	fileWriter.write("\tif(entity!=null){\n");
	fileWriter.write("\t\t" + daoName + ".saveT(entity);\n");
	fileWriter.write("\t\treturn entity.getId();\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn 0l;\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic " + fileName + "Response getById(Long id){\n");
	fileWriter
	.write("\treturn " + fileName + "Converter.getResponseFromEntity(" + daoName + ".getTById(id));\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic List<" + fileName + "Response> getAll(){\n");
	fileWriter.write(
	"\treturn " + fileName + "Converter.getResponseListFromEntityList(" + daoName + ".getAllT());\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic void delete(Long id){\n");
	fileWriter.write("\t" + daoName + ".deleteT(id);\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic boolean exist(Long id){\n");
	fileWriter.write("\tif(getById(id)!=null){\n");
	fileWriter.write("\t\treturn true;\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn false;\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\t@Override\n");
	fileWriter.write("\tpublic boolean update(" + fileName + "Response request){\n");
	fileWriter.write("\t" + fileName + " entity=" + daoName + ".getTById(request.get"+fileName+"Id());\n");
	fileWriter.write("\t" + fileName + "Converter.getEntityFromResponse(request,entity);\n");
	fileWriter.write("\tif(entity!=null){\n");
	fileWriter.write("\t\t" + daoName + ".updateT(entity);\n");
	fileWriter.write("\t\treturn true;\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn false;\n");
	fileWriter.write("\t}\n\n");
	for (TableData data : fieldsMap) {
	if (data.isUnique()) {
	fileWriter.write("\tpublic boolean existBy" + getMethodeName(data.getName()) + "(" + data.getType()
	+ " " + data.getName() + "){\n");
	fileWriter.write(
	"\tif(getBy" + getMethodeName(data.getName()) + "(" + data.getName() + ")!=null){\n");
	fileWriter.write("\t\treturn true;\n");
	fileWriter.write("\t}\n");
	fileWriter.write("\treturn false;\n");
	fileWriter.write("\t}\n\n");

	fileWriter.write("\tpublic " + fileName + "Response getBy" + getMethodeName(data.getName()) + "("
	+ data.getType() + " " + data.getName() + "){\n");
	fileWriter.write("\treturn " + fileName + "Converter.getResponseFromEntity(" + daoName + ".getBy"
	+ getMethodeName(data.getName()) + "(" + data.getName() + "));");
	fileWriter.write("\t}\n\n");
	}
	}

	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("ServiceImpl Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createService(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Service.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".services;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".requests.dto." + fileName + "Request;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".responses.dto." + fileName + "Response;\n");
	fileWriter.write("import java.util.List;\n\n");
	fileWriter.write("public interface " + fileName);
	fileWriter.write("Service{\n");
	fileWriter.write("\tLong add(" + fileName + "Request request);\n");
	fileWriter.write("\t" + fileName + "Response getById(Long id);\n");
	fileWriter.write("\tList<" + fileName + "Response> getAll();\n");
	fileWriter.write("\tvoid delete(Long id);\n");
	fileWriter.write("\tboolean exist(Long id);\n");
	fileWriter.write("\tboolean update(" + fileName + "Response request);\n");
	for (TableData data : fieldsMap) {
	if (data.isUnique()) {
	fileWriter.write("\tboolean existBy" + getMethodeName(data.getName()) + "(" + data.getType() + " "
	+ data.getName() + ");\n");
	fileWriter.write("\t" + fileName + "Response getBy" + getMethodeName(data.getName()) + "("
	+ data.getType() + " " + data.getName() + ");\n");
	}
	}
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Service Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createConverter(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Converter.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".utils;\n\n");

	fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
	fileWriter.write("import java.util.ArrayList;\n");
	fileWriter.write("import java.util.List;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".requests.dto." + fileName + "Request;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".responses.dto." + fileName + "Response;\n\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("Converter {\n");

	fileWriter.write("\n\tpublic static " + fileName + "Request getSample() {\n");
	fileWriter.write("\t\t" + fileName + "Request response = new " + fileName + "Request();\n");
	for (TableData entry : fieldsMap) {
	System.out.println("name:"+entry.getName());
	fileWriter.write("\t\tresponse.set" + getMethodeName(entry.getName()) + "(" + getSample(entry.getType(),entry.getName())
	+ ");\n");
	}
	fileWriter.write("\t\treturn response;\n");
	fileWriter.write("\t}\n");

	fileWriter.write(
	"\n\tpublic static " + fileName + " getEntityFromRequest(" + fileName + "Request request) {\n");
	fileWriter.write("\t\tif(request!=null){\n");
	fileWriter.write("\t\t\t" + fileName + " response = new " + fileName + "();\n");
	for (TableData entry : fieldsMap) {
	fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
	+ getMethodeName(entry.getName())
	+ "());\n");
	}
	fileWriter.write("\t\t\treturn response;\n");
	fileWriter.write("\t\t}\n");
	fileWriter.write("\t\treturn null;\n");
	fileWriter.write("\t}\n");

	fileWriter.write(
	"\n\tpublic static " + fileName + "Response getResponseFromEntity(" + fileName + " request) {\n");
	fileWriter.write("\t\tif(request!=null){\n");
	fileWriter.write("\t\t\t" + fileName + "Response response = new " + fileName + "Response();\n");
	fileWriter.write("\t\t\tresponse.set" + fileName + "Id(request.getId());\n");
	for (TableData entry : fieldsMap) {
	fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
	+ getMethodeName(entry.getName())
	+ "());\n");
	}
	fileWriter.write("\t\t\treturn response;\n");
	fileWriter.write("\t\t}\n");
	fileWriter.write("\t\treturn null;\n");
	fileWriter.write("\t}\n");

	fileWriter.write(
	"\n\tpublic static " + fileName + " getEntityFromResponse(" + fileName + "Response request,"+fileName+" response) {\n");
	fileWriter.write("\t\tif(request!=null){\n");
	for (TableData entry : fieldsMap) {
		if(!entry.isUnique()){
			fileWriter.write("\t\t\tresponse.set" + getMethodeName(entry.getName()) + "(request.get"
					+ getMethodeName(entry.getName())
					+ "());\n");			
		}
	}
	fileWriter.write("\t\t\treturn response;\n");
	fileWriter.write("\t\t}\n");
	fileWriter.write("\t\treturn null;\n");
	fileWriter.write("\t}\n");

	fileWriter.write("\n\tpublic static List<" + fileName + "Response> getResponseListFromEntityList(List<"
	+ fileName + "> requestList) {\n");
	fileWriter.write("\t\tif(CollectionUtils.isNotEmpty(requestList)){\n");
	fileWriter.write("\t\tList<" + fileName + "Response> responseList = new ArrayList<>();\n");
	fileWriter.write("\t\tfor(" + fileName + " request:requestList){\n");
	fileWriter.write("\t\t\tresponseList.add(getResponseFromEntity(request));\n");
	fileWriter.write("\t\t}\n");
	fileWriter.write("\t\treturn responseList;\n");
	fileWriter.write("\t\t}\n");
	fileWriter.write("\t\treturn null;\n");
	fileWriter.write("\t}\n");

	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Converter Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static String getSample(String value, String name) {
	switch (value) {
	case "String":
	return "\"" + name + "\"";
	case "Double":
	case "Float":
	case "Integer":
	return "123";
	case "Long":
	return "123l";
	case "Boolean":
	return "true";
	case "List<String>":
	return "new ArrayList<>()";
	default:
	return "new " + value + "()";
	}
	}

	private static void createResponse(String folder, String fileName) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Response.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".responses.dto;\n\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".pojos." + fileName + "Pojo;\n");
	fileWriter.write("import lombok.Data;\n\n");
	fileWriter.write("@Data\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("Response extends " + fileName + "Pojo{\n");
	fileWriter.write("\tprivate Long " + fileName.toLowerCase().charAt(0)
	+ fileName.substring(1, fileName.length()) + "Id;\n");
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Response Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createRequest(String folder, String fileName) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Request.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".requests.dto;\n\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".pojos." + fileName + "Pojo;\n");
	fileWriter.write("import lombok.Data;\n\n");
	fileWriter.write("@Data\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("Request extends " + fileName + "Pojo{\n");
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Request Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createPojo(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Pojo.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".pojos;\n\n");
	fileWriter.write("import lombok.Data;\n\n");
	fileWriter.write("@Data\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("Pojo{\n");
	for (TableData data : fieldsMap) {
	fileWriter.write("\tprivate " + data.getType() + " " + data.getName() + ";\n");
	}
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Pojo Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createDaoImpl(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "DaoImpl.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".dao.impl;\n");
	fileWriter.write("import org.springframework.stereotype.Repository;\n");
	fileWriter.write("import javax.transaction.Transactional;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".dao." + fileName + "Dao;\n");
	fileWriter.write("@Repository\n");
	fileWriter.write("@Transactional\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write("DaoImpl extends AbstractDaoImpl<" + fileName + "> implements " + fileName + "Dao{\n");

	fileWriter.write("\tpublic " + fileName + "DaoImpl() {\n");
	fileWriter.write("\t\tsuper(" + fileName + ".class);\n");
	fileWriter.write("\t}\n\n");
	for (TableData tableData : fieldsMap) {
	if (tableData.isUnique()) {
	fileWriter.write("\tpublic " + fileName + " getBy" + getMethodeName(tableData.getName()) + "("
	+ tableData.getType() + " " + tableData.getName() + "){\n");
	fileWriter.write("\t\treturn getT(FROM_ENTITY + \" where \" + "+fileName+".Columns."+getUppercase(tableData.getName())+" + \"='\" + "+tableData.getName()+"+\"'\");\n");
	fileWriter.write("\t}\n\n");
	}
	}

	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("DaoImpl Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createDao(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + "Dao.java");
	fileWriter.write("package " + MAIN_PACKAGE + ".dao;\n");
	fileWriter.write("import " + MAIN_PACKAGE + ".entity." + fileName + ";\n");
	fileWriter.write("public interface " + fileName);
	fileWriter.write("Dao extends IAbstractDao<"+fileName+">{\n");
	for (TableData tableData : fieldsMap) {
	if (tableData.isUnique()) {
	fileWriter.write("\t" + fileName + " getBy" + getMethodeName(tableData.getName()) + "("
	+ tableData.getType() + " " + tableData.getName() + ");\n");
	}
	}
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Dao Created!");

	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}

	private static void createEntity(String folder, String fileName, List<TableData> fieldsMap) {
	try {
	FileWriter fileWriter = new FileWriter(folder + fileName + ".java");
	fileWriter.write("package " + MAIN_PACKAGE + ".entity;\n\n");
	fileWriter.write("import javax.persistence.Column;\n");
	fileWriter.write("import javax.persistence.Entity;\n");
	fileWriter.write("import javax.persistence.Table;\n");
	fileWriter.write("import lombok.Data;\n");
	fileWriter.write("@Data\n");
	fileWriter.write("@Entity\n");
	fileWriter.write("@Table(name=" + fileName + ".Columns.TABLE)\n");
	fileWriter.write("public class " + fileName);
	fileWriter.write(" extends BaseEntity{\n");
	fileWriter.write("\tpublic interface Columns{\n");
	fileWriter.write("\t\tString TABLE=\"" + fileName.toLowerCase().charAt(0)
	+ fileName.substring(1, fileName.length()) + "s\";\n");
	fileWriter.write("\t\tString QUERY=\"CREATE TABLE "+ fileName.toLowerCase().charAt(0)
	+ fileName.substring(1, fileName.length()) +"s (id INT AUTO_INCREMENT,");
	for (TableData data : fieldsMap) {
	fileWriter.write(data.getName()+" "+data.getDataType());
	if(data.isNotNull()) {
	fileWriter.write(" NOT NULL");	
	}
	if(data.isUnique()) {
	fileWriter.write(" UNIQUE");	
	}
	fileWriter.write(",");
	}
	fileWriter.write("created DATETIME,updated DATETIME,creator INTEGER,updator INTEGER,PRIMARY KEY (id));\";\n");
	for (TableData data : fieldsMap) {
	fileWriter.write("\t\tString " + getUppercase(data.getName()) + " =\"" + data.getName() + "\";\n");
	}
	fileWriter.write("\t}\n\n");
	for (TableData data : fieldsMap) {
	fileWriter.write("\t@Column(name = Columns." + getUppercase(data.getName()));
	fileWriter.write(", nullable="+!data.isNotNull());	
	fileWriter.write(", unique="+!data.isNotNull());	
	if(!StringUtils.isEmpty(data.getDataType())) {
	fileWriter.write(", columnDefinition=\""+data.getDataType()+"\"");
	}
	
	fileWriter.write(")\n");
	fileWriter.write("\tprivate " + data.getType() + " " + data.getName() + ";\n");
	}
	fileWriter.write("}");
	fileWriter.flush();
	fileWriter.close();
	System.out.println("Entity Created!");
	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}
	public static String getUppercase(String s) {
	char[] charArray = s.toCharArray();
	String upper = "";
	for (char c : charArray) {
	if (c >= 'A' && c <= 'Z') {
	if (upper.length() > 0) {
	upper = upper + "_";
	}
	}
	upper = upper + String.valueOf(c).toUpperCase();
	}
	return upper;
	}

	public static String getMethodeName(String name) {
	return getUppercase(name).charAt(0) + name.substring(1, name.length());
	}
}