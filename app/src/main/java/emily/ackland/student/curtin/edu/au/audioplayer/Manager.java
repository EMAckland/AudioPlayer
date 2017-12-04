package emily.ackland.student.curtin.edu.au.audioplayer;



import java.util.List;

public class Manager {
	BaseActivity baseActivity;
	List<ManagerInterface> fragments;

	public Manager(BaseActivity baseActivity) {
		this.baseActivity = baseActivity;
		this.fragments = null;
	}

}
