import org.web.comment.douban.CommentTask;


public class HiveCli {
public static void main(String[] args) {
		
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
		}
	}
}
