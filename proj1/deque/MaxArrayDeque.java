package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item>{
    private Comparator<Item> comparator;
    public MaxArrayDeque(Comparator<Item> c){
        super();
        comparator = c;
    }
    public Item max(){
        if(isEmpty()){
            return null;
        }else{
            Item max = get(0);
           for(int i = 0;i<size();i++){
               if(comparator.compare(get(i),max)>0){
                   max = get(i);
               }
           }
           return max;
        }
    }
    public Item max(Comparator<Item> c){
        if(isEmpty()){
            return null;
        }else {
            Item max = get(0);
            for(int i = 0;i<size();i++){
                if(c.compare(get(i),max)>0){
                    max = get(i);
                }
            }
            return max;
        }
    }
}
