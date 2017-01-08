package lingh.dao.impl;

import java.util.ArrayList;
import java.util.List;

import lingh.dao.CnBlogDao;
import lingh.entity.CnBlogDetail;
import lingh.util.DBHelper;

/**
 * 
CREATE TABLE `spider_cn_blog_detail` (
  `post_id` int(11) NOT NULL,
  `url` varchar(1024) DEFAULT NULL,
  `title` varchar(1024) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `comment_count` int(11) DEFAULT NULL,
  `content` mediumtext,
  `content_images` mediumtext,
  `new_content` mediumtext,
  `comments` mediumtext,
  `tags` varchar(1024) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 *
 */
public class CnBlogDaoImpl implements CnBlogDao{

	@Override
	public int saveDetail(CnBlogDetail detail) {
		DBHelper dbhelper = new DBHelper();
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO spider_cn_blog_detail(post_id, url, title, time, view_count, comment_count, content, content_images, new_content, comments, tags, author, create_time)")
		.append("VALUES (?, ?, ?, ?,?,  ?, ?, ?, ?, ?, ?, ?, now())");
		// …Ë÷√ sql values µƒ÷µ
	    List<String> sqlValues = new ArrayList<String>();
	    sqlValues.add(detail.getPostId());
	    sqlValues.add(detail.getUrl());
	    sqlValues.add(detail.getTitle());
	    sqlValues.add(detail.getTime());
	    sqlValues.add(detail.getViewCount());
	    sqlValues.add(detail.getCommentCount());
	    sqlValues.add(detail.getContent());
	    sqlValues.add(detail.getContentImages());
	    sqlValues.add(detail.getNewContent());
	    sqlValues.add(detail.getComments());
	    sqlValues.add(detail.getTags());
	    sqlValues.add(detail.getAuthor());
	    int result = dbhelper.executeUpdate(sql.toString(), sqlValues);
	    return result;
	}

}
