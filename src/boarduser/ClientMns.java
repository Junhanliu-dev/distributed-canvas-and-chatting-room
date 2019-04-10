package boarduser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientMns {

    private HashMap<Integer, String> userPool = new HashMap<Integer, String>();
    private CanvasBoard activeCanvas;
    //private Chat chatRoom;
    private int userID;
    private String canvasFile = "CanvasCollection.dat";
    private HashMap<String, List<GraphObj>> canvasCollection = new HashMap<>();

    public ClientMns(String userName) {// , CanvasBoard activeCanvas) {
        userID = 0;
        userPool.put(userID, userName);
        System.out.println("boss created");

        /*Testing method for user list update*/
        /*userPool.put(2, "Leo");
        userPool.put(3, "Dan");
        userPool.put(4, "2B");*/
    }

    public void setActiveCanvas(CanvasBoard activeCanvas) {
        this.activeCanvas = activeCanvas;
    }

    public boolean checkCanvas(String canvasName) {
        return canvasCollection.containsKey(canvasName);
    }

    /* canvas management section */
    public void readCanvasList() {
        try {
            ObjectInputStream fileHandle = new ObjectInputStream(new FileInputStream(canvasFile));
            Object content = fileHandle.readObject();
            canvasCollection = (HashMap<String, List<GraphObj>>) content;
            fileHandle.close();
        } catch (ClassNotFoundException e) {
            System.out.println("File not found, opening empty canvas");
        } catch (IOException e) {
            System.out.println("File not found, opening empty canvas");
        }
    }

    public String[] getCanvasList() {
        Object[] list = canvasCollection.keySet().toArray();
        String[] finalString = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            finalString[i] = list[i].toString();

        }
        return finalString;
    }

    public boolean deleteCanvas(String canvasName) {
        if (canvasCollection.containsKey(canvasName)) {
            canvasCollection.remove(canvasName);
            return true;
        }
        return false;
    }

    public void createNewCanvas(String canvasName) {

        List<GraphObj> newCanvasContent = new CopyOnWriteArrayList<GraphObj>();
        // canvasCollection.put(canvasName,newCanvasContent);

        activeCanvas.setCanvasName(canvasName);
        activeCanvas.reloadCanvasContent(newCanvasContent);
        activeCanvas.channel.sendSwitch();
        // need canvas support
    }

    public void openCanvas(String canvasName) throws NotExistException {

        if (canvasCollection.containsKey(canvasName)) {

            List<GraphObj> canvasContent = canvasCollection.get(canvasName);

            activeCanvas.setCanvasName(canvasName);
            activeCanvas.reloadCanvasContent(canvasContent);
            activeCanvas.channel.sendSwitch();
 
        } else {

            throw new NotExistException("The canvas requested not exist");
        }

    }

    // public void setCanvas(ArrayList<GraphObj> canvasObjects) {
    // activeCanvas = canvasObjects;
    // }
    public void saveCanvas(List<GraphObj> canvasContent) {

        System.out.println("canvas Saving");

        String canvasName = activeCanvas.getCanvasName();
        System.out.println("canvast" + canvasContent.size());

        canvasCollection.put(canvasName, canvasContent);
        try
        {
        	this.flushCanvasFile();
        }catch(IOException e)
        {
        	System.out.println("Cannot save the file");
        }

    }

    public void flushCanvasFile() throws IOException {

        System.out.println("saving to file...");
        ObjectOutputStream fileHandle = new ObjectOutputStream(new FileOutputStream(canvasFile));
        fileHandle.writeObject(canvasCollection);
        fileHandle.close();
    }

    // public void saveCanvasAsImage(CanvasBoard canvas) {
    // need to talk to Leo. No!!!!!!!!! Leo do not want to talk to you!
    // }

    /* user management section */
    public int grantAccess(String username) {
        if (!isUnique(username)) {
            userID++;
            userPool.put(userID, username);
            return userID;
        } else {
            return -1;
        }

    }

    public String denyAccess(int userID) {
        userPool.remove(userID);
        return "Access Denied."; // need to close thread too
    }
    
    public int denyAccess(String userName){
        Iterator<Integer> iterKey = userPool.keySet().iterator();
        while(iterKey.hasNext())
        {
            int userID = iterKey.next();
            if(userPool.get(userID).equals(userName))
            {
                denyAccess(userID);
                return userID;
            }
        }
        return -10;
    }

    public HashMap<Integer, String> getUserInfo() {
        return userPool;
    }

    public boolean isLegit(int userID) {
        return userPool.containsKey(userID);
    }

    public boolean isUnique(String userName) {
        return userPool.values().contains(userName);
    }
    /* message */

}
