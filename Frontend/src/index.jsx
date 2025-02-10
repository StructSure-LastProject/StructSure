import "./index.css"
import "typeface-poppins";

/* @refresh reload */
import { render } from 'solid-js/web';
import { Router } from '@solidjs/router';
import App from './App';

const root = document.getElementById('root')

render(() => (
  <App />
)
, root)
