import { createSignal } from "solid-js";

/**
 * This function is to send requests to the backend
 * @returns {fetchData, data, loading, error, statusCode} response to the request 
 */
const useFetch = () => {
  const [data, setData] = createSignal(null);
  const [image, setImage] = createSignal(null);
  const [loading, setLoading] = createSignal(true);
  const [error, setError] = createSignal(null);
  const [statusCode, setStatusCode] = createSignal(0);

  /**
   * Will fetch data from server
   * @param {Navigator} navigate function to redirect to login if not authenticated
   * @param {String} endpoint the endpoint
   * @param {Object} requestData the object containing request informations
   */
  const fetchData = async (navigate, endpoint, requestData) => {
    setLoading(true);
        try {
            if (localStorage.getItem("token")) {
                if (!requestData.headers) requestData.headers = {};
                requestData.headers.Authorization = `Bearer ${localStorage.getItem("token")}`
            }
            const response = await fetch(endpoint, requestData);
            setStatusCode(response.status);
            if (response.ok) {    
                const jsonData = await response.json();
                renewToken(response)
                setData(jsonData);
            } else {
                if (response.status === 401) {
                    const currentRoute = window.location.pathname + window.location.search
                    if (!currentRoute.startsWith("/login") && !currentRoute.startsWith("/account")) {
                        localStorage.setItem("loginForward", currentRoute);
                    }
                    navigate("/login");
                    return
                }
                const errorData = await response.json();
                setError({
                    statusCode: response.status,
                    errorData
                });
            }
        } catch (err) {
            const errorData = { message: err.message || "Network or server error occurred" };
            setError({
                statusCode: 500,
                errorData
            });
        } finally {
            setLoading(false);
        }   
    };

  /**
   * Check if a new token has been given by the server and updates the
   * local one.
   * @param {Response} response the object containing response informations
   */
    const renewToken = (response) => {
        const token = response.headers.get("Authorization");
        if (token != null) localStorage.setItem("token", token)
    }

    /**
     * Will fetch an image from the server
     * @param {Navigator} navigate the endpoint
     * @param {String} endpoint the endpoint
     * @param {Object} requestData the object containing request informations
     */
    const fetchImage = async (navigate, endpoint, requestData) => {
        setLoading(true);
        try {
            if (localStorage.getItem("token")) {
                if (!requestData.headers) requestData.headers = {};
                requestData.headers.Authorization = `Bearer ${localStorage.getItem("token")}`
            }
            const response = await fetch(endpoint, requestData);
            setStatusCode(response.status);

            if (response.ok) {
                const imageBlob = await response.blob();
                const imageUrl = URL.createObjectURL(imageBlob);
                setImage(imageUrl);
            } else {
                if (response.status === 401) {
                    const currentRoute = window.location.pathname + window.location.search
                    if (!currentRoute.startsWith("/login") && !currentRoute.startsWith("/account")) {
                        localStorage.setItem("loginForward", currentRoute);
                    }
                    navigate("/login");
                    return
                }
                const errorData = await response.json();
                setError({
                    statusCode: response.status,
                    errorData
                });
            }
        } catch (err) {
        const errorData = { message: err.message || "Network or server error occurred" };
            setError({
                statusCode: 500,
                errorData
            });
        } finally {
            setLoading(false);
        }
    };
  

  return { fetchData, data, loading, error, statusCode, fetchImage, image };
};

export default useFetch;
