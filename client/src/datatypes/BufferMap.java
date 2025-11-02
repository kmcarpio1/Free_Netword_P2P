package src.datatypes;
import java.util.*;

import src.config.ConfigClient;

public class BufferMap {
    private boolean[] _bufferMap;
    private int _pieceSize;

    public BufferMap(long fileSize, int pieceSize, boolean have){
        _pieceSize = pieceSize;
        _bufferMap = new boolean[(int)Math.ceil((double)fileSize / pieceSize)];
        for(int k = 0; k<_bufferMap.length; k++){
            _bufferMap[k] = have; 
        }
    }

    public BufferMap(int bufferSize, int pieceSize, boolean have){
        _pieceSize = pieceSize;
        _bufferMap = new boolean[bufferSize];
        for(int k = 0; k<_bufferMap.length; k++){
            _bufferMap[k] = have; 
        }
    }


    public BufferMap(long fileSize, int pieceSize, boolean[] have){
        _pieceSize = pieceSize;
        _bufferMap = new boolean[(int)Math.ceil((double)fileSize / pieceSize)];
        for(int k = 0; k<_bufferMap.length; k++){
            _bufferMap[k] = have[k]; 
        }
    }

    public BufferMap(int size, boolean have){
        _pieceSize = ConfigClient.getPieceSize();
        _bufferMap = new boolean[size];
        for(int k = 0; k<size; k++){
            _bufferMap[k] = have; 
        }
    }

    public BufferMap(int size, boolean[] have){
        _pieceSize = ConfigClient.getPieceSize();
        _bufferMap = new boolean[size];
        for(int k = 0; k<size; k++){
            _bufferMap[k] = have[k]; 
        }
    }

    /**
     * Constructeur à partir d'un nombre hexadécimal (un string)
     * @param hex
     */
    public BufferMap(String hexFull){
        String hex = hexFull.split("/")[0];
        String size = hexFull.split("/")[1];

        _bufferMap = new boolean[Integer.parseInt(size, 16)];
        _pieceSize = ConfigClient.getPieceSize();

        int index = 0;
        for (int i = 0; i < hex.length(); i++) {
            if (index>=Integer.parseInt(size, 16)) break;
            char c = hex.charAt(i);
            int value = Character.digit(c, 16);
            String binary = String.format("%04d", Integer.parseInt(Integer.toBinaryString(value)));
            for (int j = 0; j < 4; j++) {
                _bufferMap[index++] = binary.charAt(j) == '1';
                if (index >= _bufferMap.length) return;
            }
        }
        
    }

    /**
     * @return the size of the bufferMap (changed name from "nPiecesMax" to adapt to List interface)
     */
    public int size(){
        return _bufferMap.length;
    }

    /**
     * @return the size of the pieceSize 
     */
    public int getPieceSize(){
        return _pieceSize;
    }  

     /**
      * @return the number of bytes the peer has 
      */ 
      public int nPieces(){
        int n = this.size(); 
        int count = 0;
        for (int i = 0; i < n; i++){
            if (this._bufferMap[i]) count++; 
        }
        return count;
    }

    public boolean[] getBufferMap(){
        return _bufferMap;
    }

    public void setReceiveBufferMap(int index){
        _bufferMap[index]=true;
    }

    public void setDeleteBufferMap(int index){
        _bufferMap[index]=false;
    }


    /**
     * It modifie this buffer as a addition operation. If this[i] or b[i] is true then this[i] will be true, 
     * and if both are false, then this[i] will be false
     * @param buffermap to add
     */
    public void addsTrueness(BufferMap b)throws Exception{
        int l = b.size();
        boolean[] bBuffer = b.getBufferMap();
        
        if (l != this.size()) throw new Exception ("bufermaps have not the same piece size or the same length");
        else {
            for (int i = 0; i < l; i=i+1){
                if (bBuffer[i]) {
                    this.setReceiveBufferMap(i);
                }
            }       
        }
    }


    /**
     * @param buffermap to compare
     * @return list of indexes i such that b[i] true and this[i] false.
     */
    public BufferMap compareTrueness(BufferMap b)throws Exception{
        int l = b.size();
        int s = b.getPieceSize();
        boolean[] thisBuffer = this.getBufferMap();
        boolean[] bBuffer = b.getBufferMap();
        

        BufferMap toret = new BufferMap(l, s, false);
        if (l != this.size()) throw new Exception ("bufermaps have not the same piece size or the same length");
        else {
            for (int i = 0; i < l; i=i+1){
                if ( !thisBuffer[i] && bBuffer[i] ) toret.setReceiveBufferMap(i);
            }       
            return toret;
        }
    }


    /**
     * It modifie this buffer as a addition operation. If this[i] or b[i] is true then this[i] will be true, 
     * and if both are false, then this[i] will be false
     * @param buffermap to compare and add
     * @return list of indexes i such that b[i] true and this[i] false.
     */
    public BufferMap addAndCompareTrueness(BufferMap b)throws Exception{
        int l = b.size();
        int s = b.getPieceSize();
        boolean[] thisBuffer = this.getBufferMap();
        boolean[] bBuffer = b.getBufferMap();
        

        BufferMap toret = new BufferMap(l, s, false);


        if (l != this.size()) throw new Exception ("bufermaps have not the same piece size or the same length");
        else {
            for (int i = 0; i < l; i=i+1){
                if ( !thisBuffer[i] && bBuffer[i] ) {
                    toret.setReceiveBufferMap(i);
                    this.setReceiveBufferMap(i);
                }
            }  
            return toret;
        }
    }



    //perform OR logic operation
    public boolean or(){
        boolean[] thisBuffer = this.getBufferMap();
        boolean acc = false ; 
        for (int i = 0; i < thisBuffer.length; i++){
            acc = acc || thisBuffer[i];
        }
        return acc ;
    }

    @Override
    public String toString(){
        long acc = 0;
        long p2 = 1;
        for (int i=0 ; i<_bufferMap.length ; i++){
            if (_bufferMap[i]) acc += p2;
            p2 *= 2;
        }
        String toRet = Long.toHexString(acc) + "/" + Long.toHexString(_bufferMap.length);
        return toRet;
    }

}
