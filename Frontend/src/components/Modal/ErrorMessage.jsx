/**
 * Displays a message (typically an error) in red text
 * @param {Object} props - Component properties
 * @param {string} props.message - The message to display
 */
const ErrorMessage = ({ message }) => (
  <div class="mb-4 text-[#F13327] text-sm font-medium">
    {message}
  </div>
);
export default ErrorMessage;