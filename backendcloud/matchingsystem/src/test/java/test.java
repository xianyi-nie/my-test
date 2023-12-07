import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class test {
    public static void main(String[] args) {
//        MultiValueMap<String, String> map = new LinkedMultiValueMap();
//        map.add("user_id", "001");
//        map.add("user_id", "002");
//        map.add("rating", "300");
//        map.add("rating", "500");
//        System.out.println(map.getFirst("user_id"));
//        System.out.println(map.getFirst("rating"));
        List<Integer> players = new ArrayList<>();
        players.add(3);
        players.add(15);
        players.add(12);
        players.add(1);
        players.add(23);
        players.add(5);
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i));
            if(players.get(i) > 10){
                players.remove(i);
                i--;
            }
        }
        System.out.println(players);
    }

}
