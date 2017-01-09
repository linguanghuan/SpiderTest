package lingh.entity;

public class ExtraTorrentDetail {
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getMagnet() {
		return magnet;
	}
	public void setMagnet(String magnet) {
		this.magnet = magnet;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getSeeds() {
		return seeds;
	}
	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}
	public String getLeechers() {
		return leechers;
	}
	public void setLeechers(String leechers) {
		this.leechers = leechers;
	}
	public String getHealth() {
		return health;
	}
	public void setHealth(String health) {
		this.health = health;
	}
	private String subject;
	private String country;
	public String getTorrentLink() {
		return torrentLink;
	}
	public void setTorrentLink(String torrentLink) {
		this.torrentLink = torrentLink;
	}
	public String getTorrentBase64() {
		return torrentBase64;
	}
	public void setTorrentBase64(String torrentBase64) {
		this.torrentBase64 = torrentBase64;
	}
	private String torrentLink;
	private String torrentBase64;
	private String magnet;
	private String author;
	private String time;
	private String size;
	private String seeds;
	private String leechers;
	private String health;

}
