package whiteBoard;

// The callback interface aims for event driven programming
// Notify the client when a whiteboard event is generated
public interface WhiteBoardEventListener {
    void onWhiteBoardMsg(String message);

    void onImg(byte[] img);

    void onWhiteBoardClose();
}
