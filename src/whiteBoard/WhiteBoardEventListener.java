package whiteBoard;


public interface WhiteBoardEventListener {
    void onWhiteBoardMsg(String message);

    void onImg(byte[] img);

    void onWhiteBoardClose();
}
