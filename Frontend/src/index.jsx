/* @refresh reload */
import { render } from 'solid-js/web';
import { Router } from "@solidjs/router";
import './index.css';
import Login from './pages/Login.jsx';

const root = document.getElementById('root')


const App = (props) => (
    <>
      <h1>Site Title</h1>
      {props.children}
    </>
  );

render(() => <Login />, root)
