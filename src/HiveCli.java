import org.web.Master;
import org.web.comment.douban.CommentTask;
import org.web.sites.DianPing;
import org.web.sites.JD;


public class HiveCli {
public static void main(String[] args) {
		
	/*
		if(args.length!=1)
		{
			System.err.println("Usage:<role>");
			System.exit(1);
		}
		CommentTask task = new CommentTask();

		int role=Integer.parseInt(args[0]);
		
		if(role==0)
		{
		task.getAllCommentListMul("output/douban/booklist_uniq.txt",
				"output/douban/commentlist.txt");
		}
		else if(role==1)
		{
			task.getAllCommentListMul("output/douban/booklist_uniq1.txt",
					"output/douban/commentlist.txt");
		}
		else if(role==2)
		{
			task.getAllCommentListMul("output/douban/booklist_uniq2.txt",
					"output/douban/commentlist.txt");
		}*/
	
	//DianPing dp = new DianPing();
	// dp.getList("master/slaves/dianping/route.txt",
	// "master/slaves/dianping/list.txt");
	////dp.getEnities("master/slaves/dianping/list.txt",
		////	"master/slaves/dianping/entity.txt");
	
	////JD jd = new JD();
	////jd.getEnities("master/slaves/jd/list.txt",
	////		"master/slaves/jd/entity.txt");
	Master master=new Master();
	master.init();
	master.get();
	
	
	}
	
}
