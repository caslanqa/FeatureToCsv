import java.util.*;
import java.util.stream.Collectors;

public class TextConverter {
    public static String quarter;
    public static String component;
    public static String label;
    public static String assignee;
    public static String repoFolder;
    public static final List<String> headers = Arrays.asList("#","Summary","Description","Quarter","Issue Type","Component Names","Labels","Assignee Name","Test Type","Test Repository Folder");

    private static List<String> getTextList(String str){
        return Arrays.stream(str.trim().split("\\|")).map(x->x.trim()).filter(x->!x.isEmpty()).collect(Collectors.toList());
    }

    private static List<Integer> getTitleIndexes(List<String> list){
        List<Integer> temp = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            if(list.get(i).startsWith("Scenario"))
                temp.add(i);
        }
        return temp;
    }

    private static List<List<String>> getScenarioList(List<String> list){
        List<Integer> indxs = getTitleIndexes(list);
        List<List<String>> scenarioList = new ArrayList<>();
        for (int i = 0; i < indxs.size(); i++) {
            List<String> scenario = new ArrayList<>();
            int toIndx = i+1 < indxs.size()?indxs.get(i+1):list.size();
            for (int j = indxs.get(i); j < toIndx ; j++) {
                scenario.add(list.get(j));
            }
            scenarioList.add(scenario);
        }
        return scenarioList;
    }

    private static List<List<String>> multiplyWithExamples(List<String> list){
        List<List<String>> lists = getScenarioList(list);
        List<List<String>> result = new ArrayList<>();
        result.add(Arrays.asList(list.get(0)));
        for (int i = 0; i < lists.size(); i++) {
            List<String> scenario = lists.get(i);
            List<String> examples = new ArrayList<>();
            if(scenario.get(0).startsWith("Scenario Outline")){
                List<Map<String, String>> exampleList = new ArrayList<>();


                for (int j = 0; j < scenario.size(); j++) {
                    if(scenario.get(j).startsWith("Examples")){
                        examples = scenario.subList(j,scenario.size());
                        scenario = scenario.subList(0,j);
                        break;
                    }
                }

                List<String> header = getTextList(examples.get(1));
                for (int k = 2; k < examples.size(); k++) {
                    List<String> line = getTextList(examples.get(k));
                    Map<String, String> exampleLine = new HashMap<>();
                    for (int z = 0; z < line.size(); z++) {
                        exampleLine.put(header.get(z), line.get(z));
                    }
                    exampleList.add(exampleLine);
                }

                int iter = 1;

                for(Map<String, String> ex : exampleList){
                    List<String> scenarioTemp = new ArrayList<>(scenario);
                    for(String x : ex.keySet()){
                        for (int a = 1; a < scenario.size() ; a++) {
                            String str = "<"+x+">";
                            if (scenarioTemp.get(a).contains(str)) {
                                scenarioTemp.set(a, scenarioTemp.get(a).replace(str, ex.get(x)));
                            }
                        }
                    }
                    scenarioTemp.set(0,scenarioTemp.get(0).concat(" || Example = "+(iter++)));
                    result.add(scenarioTemp);
                }
            }else {
                result.add(lists.get(i));
            }


        }
        return result;
    }

    private static List<String> getDesc(List<String> abc){
        List<List<String>> list = multiplyWithExamples(abc);
        List<String> result = new ArrayList<>();
        for (int i = 1; i < list.size() ; i++) {
            String temp = list.get(0).get(0);
            List<String> x = list.get(i);
            for (String y : x){
                temp+="\n"+y;
            }
            result.add(temp);
        }
        return result;
    }

    private static List<String> getSummaries(List<String> list){
        List<List<String>> lists = multiplyWithExamples(list);
        List<String> summaries = new ArrayList<>();
        for (int i = 1; i <lists.size() ; i++) {
            String summary = (lists.get(0).get(0).trim().split(":").length<2?"":lists.get(0).get(0).trim().split(":")[1].trim())
                    .concat(" => ").concat(lists.get(i).get(0).trim().split(":").length<2?"":lists.get(i).get(0).trim().split(":")[1].trim());
            summaries.add(summary);
        }
        return summaries;
    }

    public static List<Map<String,String>> getScenarioMaps(List<String> list){
        List<String> summaries = getSummaries(list);
        List<String> descriptions = getDesc(list);
        List<Map<String,String>> scenarios = new ArrayList<>();

        for (int i = 0; i < summaries.size(); i++) {
            Map<String,String> map = new HashMap<>();
            map.put("#", String.valueOf(i+1));
            map.put("Summary",summaries.get(i));
            map.put("Description", descriptions.get(i));
            map.put("Quarter", quarter.startsWith("---")?"":quarter);
            map.put("Issue Type","Test");
            map.put("Component Names",component.startsWith("---")?"":component);
            map.put("Labels",label.startsWith("---")?"":label);
            map.put("Assignee Name",assignee.startsWith("---")?"":assignee);
            map.put("Test Type","Cucumber");
            map.put("Test Repository Folder",repoFolder.isEmpty()?"":repoFolder);
            scenarios.add(map);
        }
        return scenarios;
    }

    public static List<String> getResult(List<String> list){

        List<Map<String,String>> mapList = getScenarioMaps(list);
        List<String> result = new ArrayList<>();
        result.add(headers.stream().collect(Collectors.joining(";")));

        for(Map<String,String> map : mapList){
            String temp="";
            for(String x:headers){
                temp+=map.get(x)+";";
            }
            result.add(temp);
        }
        return result;
    }
}
