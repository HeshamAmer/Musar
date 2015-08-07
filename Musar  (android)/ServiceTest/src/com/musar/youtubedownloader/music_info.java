package com.musar.youtubedownloader;
/**
 * 
 * music info artist,audio id,rate size,reject counter,playcount
 *
 */
public class music_info {
	
	private String artist;
	private int audio_id;
	private double rate;
	private double size;
	private int reject_counter;
	private int playcount;
	
	public void setartist(String artist){this.artist=artist;}
	public void setaudio_id(int audio_id){this.audio_id=audio_id;}
	public void setPlayCount(int playcount){this.playcount=playcount;}
	public void set_rate(double rate){this.rate=rate;}
	public void set_size(double size){this.size=size;}
	public void set_reject(int reject_counter){this.reject_counter=reject_counter;}
	
	public String getartist(){return this.artist;}
	public int getPlayCount(){return this.playcount;}
	public int getaudio_id(){return this.audio_id;}
	public double get_rate(){return this.rate;}
	public double get_size(){return this.size;}
	public int get_reject(){return this.reject_counter;}
}
