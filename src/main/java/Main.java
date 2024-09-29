import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
//        1.CSV - JSON парсер;
        String initialFileName = "data.csv";
        List<Employee> listT1 = parseCSV(columnMapping, initialFileName);
        String jsonTask1 = listToJson(listT1);
        String targetFileName1 = "data1.json";
        writerInFile(targetFileName1, jsonTask1);
//        2.XML - JSON парсер;
        String xmlInitialFile = "data.xml";
        List<Employee> listT2 = parseXML(xmlInitialFile);
        String jsonTask2 = listToJson(listT2);
        String targetFileName2 = "data2.json";
        writerInFile(targetFileName2, jsonTask2);
//        3.JSON парсер;
        String jsonInitialFile = "new_data.json";
        String json = readString(jsonInitialFile);
        List<Employee> list = jsonToList(json);
        list.forEach(System.out::println);
    }

// Методы к заданию №1:
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
//        В одну строчку:
//        Gson gson = builder.create();
//        В структурный столбик для красивой печати:
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writerInFile(String fileName, String json) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

//    Метод к заданию №2:
    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
//        List<String> fields = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList1 = root.getChildNodes();
        long id = 0;
        String firstName = "";
        String lastName = "";
        String country = "";
        int age = 0;
        for (int i = 0; i < nodeList1.getLength(); i++) {
            Node nodeParent = nodeList1.item(i);
            if (nodeParent.getNodeName().equals("employee")) {
                NodeList nodeList2 = nodeParent.getChildNodes();
                for (int j = 0; j < nodeList2.getLength(); j++) {
                    Node nodeChild = nodeList2.item(j);
                    if (nodeChild.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    switch (nodeChild.getNodeName()) {
                        case "id": {
                            id = Long.parseLong(nodeChild.getTextContent());
                            break;
                        }
                        case "firstName": {
                            firstName = nodeChild.getTextContent();
                            break;
                        }
                        case "lastName": {
                            lastName = nodeChild.getTextContent();
                            break;
                        }
                        case "country": {
                            country = nodeChild.getTextContent();
                            break;
                        }
                        case "age": {
                            age = Integer.parseInt(nodeChild.getTextContent());
                            break;
                        }
                    }
                }
                Employee employee = new Employee(id, firstName, lastName, country, age);
                employeeList.add(employee);

//                    if (Node.ELEMENT_NODE == nodeChild.getNodeType()) {
//                        fields.add(nodeChild.getTextContent());
//                    }

//                employeeList.add(new Employee(Long.parseLong(fields.get(0)), fields.get(1), fields.get(2), fields.get(3), Integer.parseInt(fields.get(4))));
//                fields.clear();
            }
        }
        return employeeList;
    }

//    Методы к заданию №3
    private static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//    чтение построчно
            String s;
            while ((s = br.readLine()) != null) {
//            System.out.println(s);
                sb.append(s).append("\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    private static List<Employee> jsonToList(String jsonText) throws ParseException {
        List<Employee> employeeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Object object = parser.parse(jsonText);
        JSONArray jsonArray = (JSONArray) object;
        for (Object jsonObject : jsonArray) {
            employeeList.add(gson.fromJson(jsonObject.toString(), Employee.class));
        }
        return employeeList;
    }
}

