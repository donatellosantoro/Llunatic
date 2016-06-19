package it.unibas.lunatic.core;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.InteractiveUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.StandardUserManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserManagerProvider {

//    public static final String USER_MANAGER_STANDARD = "Standard";
//    public static final String USER_MANAGER_INTERACTIVE = "Interactive";
//    public static final String USER_MANAGER_AFTER_LLUN = "AfterLLUN";
//    public static final String USER_MANAGER_AFTER_LLUN_FORK = "AfterLLUNFork";
//    public static final String USER_MANAGER_AFTER_FORK = "AfterFork";
    private static UserManagerProvider instance;
    private OperatorFactory operatorFactory = OperatorFactory.getInstance();

    public static UserManagerProvider getInstance() {
        if (instance == null) {
            instance = new UserManagerProvider();
        }
        return instance;
    }

    public AfterForkUserManager getAfterForkUserManager() {
        return new AfterForkUserManager();
    }

    public AfterLLUNForkUserManager getAfterLLUNForkUserManager(Scenario scenario) {
        return new AfterLLUNForkUserManager(operatorFactory.getOccurrenceHandler(scenario));
    }

    public AfterLLUNUserManager getAfterLLUNUserManager(Scenario scenario) {
        return new AfterLLUNUserManager(operatorFactory.getOccurrenceHandler(scenario));
    }

    public InteractiveUserManager getInteractiveUserManager() {
        return new InteractiveUserManager();
    }

    public StandardUserManager getStandardUserManager() {
        return new StandardUserManager();
    }

    public Collection<IUserManager> getAll(Scenario scenario) {
        List<IUserManager> list = new ArrayList<IUserManager>();
        list.add(getStandardUserManager());
        list.add(getAfterForkUserManager());
        list.add(getAfterLLUNUserManager(scenario));
        list.add(getAfterLLUNForkUserManager(scenario));
        list.add(getInteractiveUserManager());
        return list;
    }

//    public IUserManager getUserManager(String userManagerType, Scenario s) {
//        IUserManager userManager = null;
//        if (USER_MANAGER_STANDARD.equals(userManagerType)) {
//            userManager = getStandardUserManager();
//        }
//        if (USER_MANAGER_INTERACTIVE.equals(userManagerType)) {
//            userManager = getInteractiveUserManager();
//        }
//        if (USER_MANAGER_AFTER_FORK.equals(userManagerType)) {
//            userManager = getAfterForkUserManager();
//        }
//        if (USER_MANAGER_AFTER_LLUN.equals(userManagerType)) {
//            userManager = getAfterLLUNUserManager(s);
//        }
//        if (USER_MANAGER_AFTER_LLUN_FORK.equals(userManagerType)) {
//            userManager = getAfterLLUNForkUserManager(s);
//        }
//        return userManager;
//    }
}
