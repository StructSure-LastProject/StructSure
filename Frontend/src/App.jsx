import { Router, Route, useNavigate } from "@solidjs/router";
import Login from "../components/Login";
import NeedsAuthentification from "../components/NeedsAuthentification";
import { createEffect } from "solid-js";

function RequireAuth(Component) {
  return () => {
    const navigate = useNavigate();

    // Check if the token exists in localStorage
    createEffect(() => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login", { replace: true }); // Redirect to login if no token
      }
    });

    // Render the protected component if token exists
    return <Component />;
  };
}

function App() {

  return (
    <>
      <Router>
        <Route path="/login" component={Login} />
        <Route path="/header" component={RequireAuth(NeedsAuthentification)} />
      </Router>
    </>
  )
}

export default App
