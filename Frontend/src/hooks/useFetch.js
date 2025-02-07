import { createSignal } from "solid-js";

/**
 * This function is to send requests to the backend
 * @returns {fetchData, data, loading, error, statusCode} response to the request 
 */
const useFetch = () => {
  const [data, setData] = createSignal(null);
  const [loading, setLoading] = createSignal(true);
  const [error, setError] = createSignal(null);
  const [statusCode, setStatusCode] = createSignal(0);

  const fetchData = async (endpoint, requestData) => {
    setLoading(true);
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}${endpoint}`, requestData);
            setStatusCode(response.status);
            if (!response.ok) throw new Error('Network response was not ok');
            const jsonData = await response.json();
            setData(jsonData);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }   
  };

  return { fetchData, data, loading, error, statusCode };
};

export default useFetch;
