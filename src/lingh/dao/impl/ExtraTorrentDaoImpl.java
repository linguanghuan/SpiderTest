package lingh.dao.impl;

import java.util.ArrayList;
import java.util.List;

import lingh.dao.CnBlogDao;
import lingh.dao.ExtraTorrentDao;
import lingh.entity.CnBlogDetail;
import lingh.entity.ExtraTorrentDetail;
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
public class ExtraTorrentDaoImpl implements ExtraTorrentDao{

	@Override
	public int saveDetail(ExtraTorrentDetail detail) {
		DBHelper dbhelper = new DBHelper();
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO extra_to(subject, country, torrent_link, torrent_base64, magnet, " +
				"author, time, size, seeds, leechers, " +
				"health, create_time)")
		.append("VALUES (?, ?, ?, ?,?, " +
				" ?, ?, ?, ?, ?, " +
				"?,  now())");
		// …Ë÷√ sql values µƒ÷µ
	    List<String> sqlValues = new ArrayList<String>();
	    sqlValues.add(detail.getSubject());
	    sqlValues.add(detail.getCountry());
	    sqlValues.add(detail.getTorrentLink());
	    sqlValues.add(detail.getTorrentBase64());
	    sqlValues.add(detail.getMagnet());
	    
	    sqlValues.add(detail.getAuthor());
	    sqlValues.add(detail.getTime());
	    sqlValues.add(detail.getSize());
	    sqlValues.add(detail.getSeeds());
	    sqlValues.add(detail.getLeechers());
	    
	    sqlValues.add(detail.getHealth());
	    
	    int result = dbhelper.executeUpdate(sql.toString(), sqlValues);
	    return result;
	}

}
