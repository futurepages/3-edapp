package apps.com.empresadedicada;

import apps.com.empresadedicada.data.DataProvider;
import apps.com.empresadedicada.data.dummy.DummyDataProvider;
import apps.com.empresadedicada.domain.DashUser;
import apps.com.empresadedicada.event.EDEvent.BrowserResizeEvent;
import apps.com.empresadedicada.event.EDEvent.CloseOpenWindowsEvent;
import apps.com.empresadedicada.event.EDEvent.UserLoggedOutEvent;
import apps.com.empresadedicada.event.EDEvent.UserLoginRequestedEvent;
import apps.com.empresadedicada.event.EDEventBus;
import apps.com.empresadedicada.view.LoginView;
import apps.com.empresadedicada.view.MainView;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Locale;

@Title("Empresa Dedicada DashBoard")
@Theme("simple")
public class AppUI extends UI {

    private final DataProvider dataProvider = new DummyDataProvider();
    private final EDEventBus   eventbus     = new EDEventBus();
	
	@Override
	protected void init(VaadinRequest request) {

        setLocale(new Locale("pt", "BR"));

        EDEventBus.register(this);
        
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);
        
        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                        EDEventBus.post(new BrowserResizeEvent());
                    }
                });
	}
	
	   /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        DashUser user = (DashUser) VaadinSession.getCurrent().getAttribute(DashUser.class.getName());
        if (user != null && "admin".equals(user.getRole())) {
            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        DashUser user = getDataProvider().authenticate(event.getUserName(),  event.getPassword());
        VaadinSession.getCurrent().setAttribute(DashUser.class.getName(), user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }	
    
    /**
     * @return An instance for accessing the (dummy) services layer.
     */
    public static DataProvider getDataProvider() {
        return ((AppUI) getCurrent()).dataProvider;
    }

    public static EDEventBus getEventbus() {
        return ((AppUI) getCurrent()).eventbus;
    }
    

}