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
   * @param {String} endpoint the endpoint
   * @param {Object} requestData the object containing request informations
   */
  const fetchData = async (endpoint, requestData) => {
    setLoading(true);
        try {
            const response = await fetch("http://localhost:8080" + endpoint, requestData);
            setStatusCode(response.status);
            if (response.ok) {    
                const jsonData = await response.json();
                setData(jsonData);
            }
            else {
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
     * Will fetch an image from the server
     * @param {String} endpoint the endpoint
     * @param {Object} requestData the object containing request informations
     */
    const fetchImage = async (endpoint, requestData) => {
        setLoading(true);
        try {
        const response = await fetch("http://localhost:8080" + endpoint, requestData);
        setStatusCode(response.status);
    
        if (response.ok) {
            const imageBlob = await response.blob();
            const imageUrl = URL.createObjectURL(imageBlob);            
            setImage(imageUrl);
        } else {
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
