import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by yuxuanli on 10/8/17.
 */
public class CalculateResult {
    private long queryNum;
    private Deque<Bucket> bucketQueue;
    private Queue<Boolean> dataStreams;

    public CalculateResult(long queryNum, Queue<Boolean> dataStreams, Deque<Bucket> bucketQueue){
        this.queryNum = queryNum;
        this.dataStreams = dataStreams;
        this.bucketQueue = bucketQueue;
    }

    // Calculate how many ones in last k data
    public int getResult(){
        int numOne = 0;
        if(queryNum <= dataStreams.size()){  // give exact data if queryNum smaller or equal than window size
            int index = dataStreams.size();
            for(boolean bit : dataStreams){
                if(index > queryNum){
                    index--;
                    continue;
                }
                if(bit == true){
                    numOne++;
                }
            }
        }
        else{ // give estimated data if queryNum larger than window size
            Iterator<Bucket> iter = bucketQueue.iterator();
            int count = 0;
            Bucket prevBucket = null;
            while(iter.hasNext()){
                Bucket cur = iter.next();
                if(cur.getPos() > queryNum){
                    if(cur.getPos() - 1 == queryNum){
                        break;
                    }
                    count -= prevBucket.getSize() / 2;
                    break;
                }
                else if(queryNum > cur.getPos()){
                    if(cur.getPos() + cur.getSize() > queryNum){
                        count += cur.getSize() / 2;
                        break;
                    }
                    count += cur.getSize();
                    prevBucket = cur;
                }
                else{
                    count += cur.getSize() / 2;
                    break;
                }
            }
            numOne = count;
        }

        return numOne;
    }
}
