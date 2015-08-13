/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.models.Tag;
import com.dubic.scribbleit.utils.IdmUtils;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author Dubic
 */
@Component
public class TagService {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private Database db;

    @Async
    public void addTags(String tags, String username) {
        if (StringUtils.isEmpty(tags)) {
            return;
        }
        //check tag exists.
        for (String name : tags.split(",")) {
            if (IdmUtils.getFirstOrNull(db.createNativeQuery("select t.name from tags t where t.name = '" + name.trim() + "'").getResultList()) == null) {
                Tag tag = new Tag();
                tag.setCreator(username);
                tag.setName(name.toLowerCase().trim());
                db.persist(tag);
                log.info("tag created : " + name);
            }
        }
    }

    public List<Object[]> getTags() {
        List<Object[]> tagList = db.createNativeQuery("select t.name from tags t").getResultList();
        return tagList;
    }

    public static void main(String[] args) {
        String[] tags = "jade,onyx,gold".split(",");
        for (int i = 0; i < tags.length; i++) {
            tags[i] = "'" + tags[i] + "'";
        }
        System.out.println(StringUtils.arrayToCommaDelimitedString(tags));
    }
}
