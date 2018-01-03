package proxy.invocationhandler;

/**
 * 个人实现类
 * 
 * @author 杨弢
 * 
 */
public class PersonBeanImpl implements PersonBean {
	String name;
	String gender;
	String interest;
	int rating;
	int ratingCount = 0;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getGender() {
		return gender;
	}

	@Override
	public String getInterest() {
		return interest;
	}

	@Override
	public int getHotOrNotRating() {
		if (ratingCount == 0) {
			return 0;
		}
		return (rating / ratingCount);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public void setInterest(String interest) {
		this.interest = interest;
	}

	@Override
	public void setHotOrNotRating(int rating) {
		this.rating += rating;
		ratingCount++;
	}
}