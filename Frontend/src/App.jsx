import { Router, Route, useNavigate } from "@solidjs/router";
import Login from "./components/Login";
import { createEffect } from "solid-js";
import Account from "./pages/Account";
import Home from "./pages/Home";
import StructSureDetail from "./pages/StructureDetail";

function RequireAuth(Component) {
  return () => {
    const navigate = useNavigate();
    createEffect(() => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login", { replace: true });
      }
    });
    return <Component />;
  };
}

function App() {

  return (
    <>
      <Router>
        <Route path="/login" component={Login} />
        <Route path="/" component={RequireAuth(Home)} />
        <Route path="/account" component={RequireAuth(Account)} />
        <Route path="/structure" component={StructSureDetail} />
      </Router>
    </>
  )
}

export default App
