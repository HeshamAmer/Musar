package musar;

public class music_info {

	private String artist;
	private int audio_id;
	private double rate;
	
	public void setartist(String artist){this.artist=artist;}
	public void setaudio_id(int audio_id){this.audio_id=audio_id;}
	public void set_rate(double rate){this.rate=rate;}
	
	public String getartist(){return this.artist;}
	public int getaudio_id(){return this.audio_id;}
	public double get_rate(){return this.rate;}
}
