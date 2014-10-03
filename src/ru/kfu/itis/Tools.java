package ru.kfu.itis;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mg on 02.10.14.
 */
public abstract class Tools {

    private static LinkedList<User> list;
    private static int level = 0;
    private static int attempts = 0;

    public static String getAnswer(String us1, String us2, String exc){
        String answer = "";
        if (exc.equals("")){
            answer = getAnswer(us1, us2);
        }else{
            int user_1 = 0;
            int user_2 = 0;
            int user_e = 0;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser parser = factory.newSAXParser();
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String req = "https://api.vk.com/method/users.get.xml?user_ids=" + us1;
                URL url = new URL(req);
                URLConnection con = url.openConnection();

                //EXCEPTION HERE!!
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                Pattern pattern = Pattern.compile("<uid>.*</uid>");
                String uId = "";
                while ((inputLine = in.readLine()) != null && uId.equals("")){
                    Matcher matcher = pattern.matcher(inputLine);
                    if (matcher.find()!=false){
                        uId = matcher.group();
                    }

                }


                in.close();
                if (!uId.equals("")){
                    Pattern p2 = Pattern.compile("[0-9]+");
                    Matcher matcher = p2.matcher(uId);
                    matcher.find();
                    user_1 = Integer.parseInt(matcher.group());


                    req = "https://api.vk.com/method/users.get.xml?user_ids=" + us2;
                    url = new URL(req);
                    con = url.openConnection();

                    //EXCEPTION HERE!!
                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));


                    //pattern = Pattern.compile("<uid>.*</uid>");
                    uId = "";
                    while ((inputLine = in.readLine()) != null && uId.equals("")){
                        matcher = pattern.matcher(inputLine);
                        if (matcher.find()!=false){
                            uId = matcher.group();
                        }

                    }

                    if (!uId.equals("")){

                        matcher = p2.matcher(uId);
                        matcher.find();
                        user_2 = Integer.parseInt(matcher.group());


                        req = "https://api.vk.com/method/users.get.xml?user_ids=" + exc;
                        url = new URL(req);
                        con = url.openConnection();

                        //EXCEPTION HERE!!
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));


                        //pattern = Pattern.compile("<uid>.*</uid>");
                        uId = "";
                        while ((inputLine = in.readLine()) != null && uId.equals("")){
                            matcher = pattern.matcher(inputLine);
                            if (matcher.find()!=false){
                                uId = matcher.group();
                            }

                        }
                        if(!uId.equals("")) {
                            list = new LinkedList<User>();
                            // System.out.println("!!!");
                            list.add(new User(user_1, 0, ""));
//                    matcher = p2.matcher(uId);
//                    matcher.find();
                            matcher = p2.matcher(uId);
                            matcher.find();
                            user_e = Integer.parseInt(matcher.group());

                            //System.out.println("!!!");


                            //EXCEPTION HERE!!
                            url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + user_1);
                            con = url.openConnection();
                            BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            //System.out.println(user_2);

                            int readId = 0;
                            while (!list.isEmpty() && readId != user_2 && level != 10) {

                                while (list.getFirst().level == level && readId != user_2) {
                                    url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + list.getFirst().id);
                                    con = url.openConnection();
                                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    readId = 0;
                                    System.out.println(list.getFirst().id);
                                    while ((inputLine = in.readLine()) != null && readId != user_2) {

//                            System.out.println("Friends");
//                            System.out.println(inputLine);
                                        if (inputLine.matches(" <uid>.+</uid>")) {
                                            matcher = p2.matcher(inputLine);
                                            matcher.find();
                                            if(Integer.parseInt(matcher.group()) != user_e) {
                                                readId = Integer.parseInt(matcher.group());
                                                list.add(new User(readId, level, list.getFirst().path + "," + list.getFirst().id));
                                            }

                                        }

                                    }
                                    list.removeFirst();
                                }
                                level++;

                            }

                            //System.out.println(list.isEmpty() + " " + level);

                            inReader.close();
                            in.close();
                            if (list.isEmpty() || level == 10) {
                                answer = "нет связи в 9 рукопожатий";
                            } else {
                                // System.out.println(list.getLast().path);
                                String[] idsOfFriends = list.getLast().path.split(",");
                                Pattern firstName = Pattern.compile("<first_name>.*</first_name>");
                                Pattern lastName = Pattern.compile("<last_name>.*</last_name>");
                                for (int i = 1; i < idsOfFriends.length; i++) {
                                    url = new URL("https://api.vk.com/method/users.get.xml?user_ids=" + idsOfFriends[i]);

                                    con = url.openConnection();
                                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    String input;
                                    while ((input = in.readLine()) != null) {
                                        Matcher matcher1 = firstName.matcher(input);
                                        Matcher matcher2 = lastName.matcher(input);
                                        if (matcher1.find()) {
                                            answer += matcher1.group().substring(12, input.length() - 15) + " ";

                                        } else {
                                            if (matcher2.find()) {
                                                answer += matcher2.group().substring(11, input.length() - 14) + " ";

                                            }
                                        }
                                    }
                                    answer += "(" + idsOfFriends[i] + ") -> ";
                                }

                                url = new URL("https://api.vk.com/method/users.get.xml?user_ids=" + list.getLast().id);

                                con = url.openConnection();
                                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                String input;

                                while ((input = in.readLine()) != null) {
                                    Matcher matcher1 = firstName.matcher(input);
                                    Matcher matcher2 = lastName.matcher(input);
                                    //System.out.println(input);


                                    if (matcher1.find()) {
                                        answer += matcher1.group().substring(12, input.length() - 15) + " ";

                                    } else {

                                        if (matcher2.find()) {
                                            answer += matcher2.group().substring(11, input.length() - 14) + " ";

                                        }
                                    }
                                }
                                answer += "(" + list.getLast().id + ")";

                            }
                        }
                    }else{
                        // System.out.println(2);
                        throw new RuntimeException();
                    }


                    in.close();
                }else{
                    // System.out.println(1);
                    throw new RuntimeException();
                }

//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
            }catch (IOException e){
                if(attempts < 10) {
                    attempts++;
                    answer = getAnswer(list.getFirst().id + "", us2, exc);
                }else{
                    answer = "error";
                }
            }

            catch (Exception e) {
                answer = "error!";
                e.printStackTrace();
            } finally {
                //System.out.println(user_1 + " " + user_2);
                level = 0;
                attempts = 0;
                try {
//                URL url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + user_1);
//                URLConnection con = url.openConnection();
//
//                //EXCEPTION HERE!!
//                BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//
//                String inputLine;
//                while ((inputLine = inReader.readLine()) != null)
//                    System.out.println(inputLine);
//                inReader.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return answer;
    }

    public static String getAnswer(String us1, String us2){

        String answer = "";
        int user_1 = 0;
        int user_2 = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser parser = factory.newSAXParser();
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String req = "https://api.vk.com/method/users.get.xml?user_ids=" + us1;
            URL url = new URL(req);
            URLConnection con = url.openConnection();

            //EXCEPTION HERE!!
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            Pattern pattern = Pattern.compile("<uid>.*</uid>");
            String uId = "";
            while ((inputLine = in.readLine()) != null && uId.equals("")){
                Matcher matcher = pattern.matcher(inputLine);
                if (matcher.find()!=false){
                    uId = matcher.group();
                }

            }


            in.close();
            if (!uId.equals("")){
                Pattern p2 = Pattern.compile("[0-9]+");
                Matcher matcher = p2.matcher(uId);
                matcher.find();
                user_1 = Integer.parseInt(matcher.group());


                req = "https://api.vk.com/method/users.get.xml?user_ids=" + us2;
                url = new URL(req);
                con = url.openConnection();

                //EXCEPTION HERE!!
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));


                //pattern = Pattern.compile("<uid>.*</uid>");
                uId = "";
                while ((inputLine = in.readLine()) != null && uId.equals("")){
                    matcher = pattern.matcher(inputLine);
                    if (matcher.find()!=false){
                        uId = matcher.group();
                    }

                }

                if (!uId.equals("")){
                    list = new LinkedList<User>();
                   // System.out.println("!!!");
                    list.add(new User(user_1, 0, ""));
//                    matcher = p2.matcher(uId);
//                    matcher.find();
                    matcher = p2.matcher(uId);
                    matcher.find();
                    user_2 = Integer.parseInt(matcher.group());

                    //System.out.println("!!!");


                    //EXCEPTION HERE!!
                    url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + user_1);
                    con = url.openConnection();
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    //System.out.println(user_2);
                    int level = 0;
                    int readId = 0;
                    while (!list.isEmpty() && readId!=user_2 && level!=10){

                        while(list.getFirst().level == level && readId!=user_2) {
                            url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + list.getFirst().id);
                            con = url.openConnection();
                            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            readId = 0;
                            //System.out.println(list.getFirst().id);
                            while ((inputLine = in.readLine()) != null && readId != user_2) {

//                            System.out.println("Friends");
//                            System.out.println(inputLine);
                                if (inputLine.matches(" <uid>.+</uid>")) {
                                    matcher = p2.matcher(inputLine);
                                    matcher.find();
                                    readId = Integer.parseInt(matcher.group());
                                    list.add(new User(readId, level, list.getFirst().path + "," + list.getFirst().id));

                                }

                            }
                            list.removeFirst();
                        }
                        level++;

                    }

                    //System.out.println(list.isEmpty() + " " + level);

                    inReader.close();
                    in.close();
                    if (list.isEmpty() || level == 10){
                        answer = "нет связи в 9 рукопожатий";
                    }else{
                       // System.out.println(list.getLast().path);
                        String[] idsOfFriends = list.getLast().path.split(",");
                        Pattern firstName = Pattern.compile("<first_name>.*</first_name>");
                        Pattern lastName = Pattern.compile("<last_name>.*</last_name>");
                        for(int i = 1; i < idsOfFriends.length; i++) {
                            url = new URL("https://api.vk.com/method/users.get.xml?user_ids=" + idsOfFriends[i]);

                            con = url.openConnection();
                            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String input;
                            while ((input = in.readLine()) != null) {
                                Matcher matcher1 = firstName.matcher(input);
                                Matcher matcher2 = lastName.matcher(input);
                                if(matcher1.find()){
                                    answer += matcher1.group().substring(12, input.length() - 15) + " ";

                                }else{
                                    if(matcher2.find()){
                                        answer += matcher2.group().substring(11, input.length() - 14) + " ";

                                    }
                                }
                            }
                            answer += "(" + idsOfFriends[i] + ") -> ";
                        }

                        url = new URL("https://api.vk.com/method/users.get.xml?user_ids=" + list.getLast().id);

                        con = url.openConnection();
                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String input;

                        while ((input = in.readLine()) != null) {
                            Matcher matcher1 = firstName.matcher(input);
                            Matcher matcher2 = lastName.matcher(input);
                            //System.out.println(input);


                            if(matcher1.find()){
                                answer += matcher1.group().substring(12, input.length() - 15) + " ";

                            }else{

                                if(matcher2.find()){
                                    answer += matcher2.group().substring(11, input.length() - 14) + " ";

                                }
                            }
                        }
                        answer += "(" + list.getLast().id + ")";

                    }
                }else{
                   // System.out.println(2);
                    throw new RuntimeException();
                }


                in.close();
            }else{
               // System.out.println(1);
                throw new RuntimeException();
            }

//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
        }catch(IOException e){
            if(attempts < 10) {
                attempts++;
                answer = getAnswer(list.getFirst().id + "", us2, "");
            }else{
                answer = "error";
            }
        }
        catch (Exception e) {
            answer = "error!";
            e.printStackTrace();
        } finally {
            //System.out.println(user_1 + " " + user_2);
            level = 0;
            attempts = 0;
            try {
//                URL url = new URL("https://api.vk.com/method/friends.get.xml?user_id=" + user_1);
//                URLConnection con = url.openConnection();
//
//                //EXCEPTION HERE!!
//                BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//
//                String inputLine;
//                while ((inputLine = inReader.readLine()) != null)
//                    System.out.println(inputLine);
//                inReader.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }
}
