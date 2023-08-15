package bvplayer.badran.bvplayer;

public class VideoFiles {
    private String id;
    private String path;
    private String title;
    private String size;
    private String dataAdded;
    private String duration;
    private String album;

    public VideoFiles(String id, String path, String title, String size, String dataAdded, String duration, String album) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.size = size;
        this.dataAdded = dataAdded;
        this.duration = duration;
        this.album = album;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }
    public String getAlbum () {
        return album;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDataAdded() {
        return dataAdded;
    }

    public void setDataAdded(String dataAdded) {
        this.dataAdded = dataAdded;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    public void setAlbum (String album) {
        this.album = album;
    }
}
