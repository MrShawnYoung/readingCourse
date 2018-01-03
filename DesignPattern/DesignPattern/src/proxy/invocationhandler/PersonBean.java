package proxy.invocationhandler;

/**
 * 个人类
 * 
 * @author 杨弢
 * 
 */
public interface PersonBean {
	String getName();

	String getGender();

	String getInterest();

	int getHotOrNotRating();

	void setName(String name);

	void setGender(String gender);

	void setInterest(String interest);

	void setHotOrNotRating(int rating);
}