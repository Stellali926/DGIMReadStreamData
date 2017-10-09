import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by yuxuanli on 10/7/17.
 */
public class DGIM {
    private int windowSize;
    public Deque<Bucket> bucketQueue; // store bucket
    public Queue<Boolean> dataStreams; // sliding window


    public DGIM(int size){
        windowSize = size;
        dataStreams = new LinkedList<>();
        bucketQueue = new ConcurrentLinkedDeque<>();

    }

    // add single number, and keep number within the window size
    public void add(boolean bit){
        Iterator<Bucket> itr = bucketQueue.iterator(); // all postions of buckets increment by one
        while(itr.hasNext()){
            Bucket cur = itr.next();
            cur.setPos(cur.getPos() + 1);
        }

        dataStreams.offer(bit);
        if(dataStreams.size() > windowSize){ // if exceeds sliding window, pop out
            dataStreams.poll();
        }

        if(!bit){
            return;
        }
        addBucket();

    }

    //if the coming bit is 1, add a new size 1 bucket
    public void addBucket(){
        bucketQueue.addFirst(new Bucket(1, 1));

        Iterator<Bucket> iter = bucketQueue.iterator();
        if(needMerge(iter)){ //check if the bucket need to merge
            Iterator<Bucket> iter1 = bucketQueue.iterator();
            mergeBuckets(iter1);
        }
    }

    // check and merge bucket from certain start of the deque
    private void mergeBuckets(Iterator<Bucket> iter) {
        iter.next();
        iter.next();
        Bucket third = iter.next();
        Bucket fourth = iter.next();
        int prevValue = third.getSize();
        third.setSize(third.getSize() + fourth.getSize()); // merge the size of two bucket
        third.setPos(third.getPos());
        iter.remove();  //remove fourth same size bucket

        Iterator<Bucket> iter1 = getIterator(prevValue);
        if(needMerge(iter1)){
            Iterator<Bucket> iter2 = getIterator(prevValue);
            mergeBuckets(iter2);
        }
    }

    // get the certain position in deque (ex. prevalue is 1, iter to certain position that next value is the bucket with size 2)
    public Iterator<Bucket> getIterator(int prevValue){
        Iterator<Bucket> iter = bucketQueue.iterator();
        int count = 0;
        while(iter.hasNext()){
            Bucket temp = iter.next();
            if(temp.getSize() == prevValue){
                count++;
            }
            if(count == 2){
                return iter;
            }
        }

        return null;
    }

    // check whether there are bucket need to merge (if there are thrid and fourth bucket wth same size)
    public boolean needMerge(Iterator<Bucket> iter){
        Bucket head, second, third, fourth;
        if(iter.hasNext()){
            head = iter.next();
            if(iter.hasNext()){
                second = iter.next();
                if(head.getSize() != second.getSize()){
                    return false;
                }
                else{
                    if(iter.hasNext()){
                        third = iter.next();
                        if(second.getSize() != third.getSize()){
                            return false;
                        }
                        else{
                            if(iter.hasNext()){
                                fourth = iter.next();
                                if(third.getSize() == fourth.getSize()){
                                    return true;
                                }
                                else{
                                    return false;
                                }
                            }
                            else{
                                return false;
                            }
                        }
                    }
                    else{
                        return false;
                    }
                }
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }


}
