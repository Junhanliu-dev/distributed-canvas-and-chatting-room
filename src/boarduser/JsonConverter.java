package boarduser;

import java.io.StringReader;
import java.util.*;

//{"message": [
//	{
//  		"content": 
//  		{
//    		"UserName":"1",
//    		"Timestamp":"2017-09-21T05:41:28",
//    		"MessageContent":"Leo kisses Dan on the ass!"
//  		}
//	},
//	{
//  		"content": 
//  		{
//    		"UserName":"1",
//    		"Timestamp":"2017-09-21T05:41:28",
//    		"MessageContent":"Leo kisses Dan on the ass!"
//  		}
//	}		
//]}
import javax.json.*;

public class JsonConverter {

    public static String convertToJson(Message msg) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        //for (int i = 0; i < chatMessage.length; i++) {
            //System.out.println(msg.getID());
            // JsonObjectBuilder builder = Json.createObjectBuilder();
            // JsonObject message =
            builder.add("content",
                    Json.createObjectBuilder().add("userName", msg.getUserName())
                            .add("timestamp", msg.getTimeStamp()).add("msgcontent", msg.getMessage()));// .build();
            // big_builder.add(message);
            // message.entrySet().forEach(s -> builder.add(s.getKey(), s.getValue()));
        //}
        JsonObject messageJson = builder.build();
        String encodedMessage = Encrypter.encrypt(messageJson.toString());

        return encodedMessage;

    }

    public static Message convertFromJson(String stringjson) {
        
        String decodedMessage = Encrypter.decrypt(stringjson);

        JsonReader jsonReader = Json.createReader(new StringReader(decodedMessage));
        JsonObject json = jsonReader.readObject();
        jsonReader.close();

        //int messageID = json.getInt("messageID");
        JsonObject content = json.getJsonObject("content");
        String userName = content.getString("userName");
        String timeStamp = content.getString("timestamp");
        String msgcontent = content.getString("msgcontent");

        Message parsedmsg = new Message(userName, msgcontent ,timeStamp);

        return parsedmsg;

    }

    public static String converToJson(HashMap<Integer, String> userPool) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder builder = factory.createArrayBuilder();
        Set<Integer> idList = userPool.keySet();
        for (Integer i : idList) {
            builder.add(factory.createObjectBuilder()
                    .add("ID", i.toString())
                    .add("UserName", userPool.get(i)));
        }
        JsonArray result = builder.build();
        return result.toString();

    }

    public static HashMap<Integer, String> convertFromJsonUser(String jsonString) {
        HashMap<Integer, String> userPool = new HashMap<Integer, String>();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        for (int i = 0; i < array.size(); i++) {
            JsonObject user = array.getJsonObject(i);
            String userIDStr = user.getString("ID");
            Integer userID = Integer.parseInt(userIDStr);
            String userName = user.getString("UserName");

            userPool.put(userID, userName);
        }

        return userPool;

    }
}
