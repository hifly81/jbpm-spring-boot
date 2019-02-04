package org.hifly.service;

import org.kie.api.task.UserGroupCallback;

import java.util.List;

public class MyUserGroupCallbackImpl implements UserGroupCallback {


    @Override
    public boolean existsUser(String s) {
        return false;
    }

    @Override
    public boolean existsGroup(String s) {
        return false;
    }

    @Override
    public List<String> getGroupsForUser(String s) {
        return null;
    }
}