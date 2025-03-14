import { Router, Route, useNavigate } from "@solidjs/router";
import Login from "./components/Login";
import { createEffect } from "solid-js";
import Account from "./pages/Account";
import Home from "./pages/Home";
import StructSureDetail from "./pages/StructureDetail";
import AdminPanel from "./pages/AdminPanel";
import useFetch from "./hooks/useFetch";

/**
 * Checks if the user have the right to acces the component
 * @param component the protected component
 * @returns the protected component if autorized, and redirection to login page if not autorized
 */
function RequireAuth(Component) {
  return () => {
    const navigate = useNavigate();
    createEffect(() => {
      const token = localStorage.getItem("token");
      if (!token) {
        const currentRoute = window.location.pathname + window.location.search
        if (!currentRoute.startsWith("/login")) localStorage.setItem("loginForward", currentRoute);
        navigate("/login", { replace: true });
      }
    });
    return <Component />;
  };
}
/**
 * Redirect to the server's 404 error page if the route is not known
 * by the frontend.
 */
function RedirectToError() {
  window.location.href = "/error404";
  return null;
}

/**
 * Defines the routes of our application
 * @returns component containing all the routes
 */
function App() {

  return (
    <>
      <Router>
        <Route path="/" component={RequireAuth(Home)} />
        <Route path="/login" component={Login} />
        <Route path="/account" component={RequireAuth(Account)} />
        <Route path="/admin-panel" component={RequireAuth(AdminPanel)} />
        <Route path="/structures/:structureId" component={RequireAuth(StructSureDetail)} />
        <Route path="/admin-panel" component={RequireAuth(AdminPanel)} />
        <Route path="*" component={RedirectToError} />
      </Router>
    </>
  )
}

export default App
