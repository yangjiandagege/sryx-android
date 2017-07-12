package com.yj.sryx.jpush;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by huxley on 17/1/10.
 */

public class LocalAliasAndTags implements Serializable{

    public String alias;

    public Set<String> tags;

    public LocalAliasAndTags(String alias, Set<String> tags) {
        this.alias = alias;
        this.tags = tags;
    }
}
