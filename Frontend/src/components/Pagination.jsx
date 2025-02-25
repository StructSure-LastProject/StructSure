import { ChevronLeft, ChevronRight } from 'lucide-solid';
import { createSignal } from 'solid-js';

/**
 * Pagination
 * @param {Number} limit The page limit
 * @param {Number} offset The offset
 * @param {Number} totalItems The total number of sensors
 * @param {Function} setOffset The setter for the offset
 * @returns The pagination component
 */
export const Pagination = ({ limit, offset, totalItems, setOffset }) => {
  const totalPages = Math.ceil(totalItems() / limit());

  const [currentPage, setCurrentPage] = createSignal(Math.floor(offset() / limit()) + 1);

  /**
   * Setter to update the pahe change
   */
  const updateCurrentPage = () => {
    setCurrentPage(Math.floor(offset() / limit()) + 1);
  };

  /**
   * Go to prev page
   */
  const handlePrevPage = () => {
    if (offset() > 0) {
      setOffset(offset() - limit());
      updateCurrentPage(); 
    }
  };

  /**
   * Go to next page
   */
  const handleNextPage = () => {
    if (offset() + limit() < totalItems()) {
      setOffset(offset() + limit());
      updateCurrentPage();
    }
  };

  /**
   * Handle page change
   * @param {Number} pageNumber 
   */
  const handlePageChange = (pageNumber) => {
    setOffset((pageNumber - 1) * limit());
    setCurrentPage(pageNumber); 
  };


  /**
   * Calculate the page numbers
   * @returns The pages numbers
   */
  const getPageNumbers = () => {
    const pages = [];
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);

      if (currentPage() > 3) pages.push('...');

      for (let i = Math.max(currentPage() - 1, 2); i <= Math.min(currentPage() + 1, totalPages - 1); i++) {
        pages.push(i);
      }

      if (currentPage() < totalPages - 2) pages.push('...');

      if (totalPages > 1) pages.push(totalPages);
    }

    return pages;
  };

  return (
    <div className="flex items-center justify-between py-5">
      <div className="flex flex-1 justify-between sm:hidden">
        <button
          onClick={handlePrevPage}
          className="relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
        >
          Précédent
        </button>
        <button
          onClick={handleNextPage}
          className="relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
        >
          Suivant
        </button>
      </div>
      <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        <div>
          <p className="text-sm text-gray-700">
            Affichage des résultats <span className="font-medium">{offset() + 1}</span> à <span className="font-medium">{Math.min(offset() + limit(), totalItems())}</span> sur{' '}
            <span className="font-medium">{totalItems()}</span>
          </p>
        </div>
        <div>
          <nav aria-label="Pagination" className="isolate inline-flex -space-x-px rounded-md shadow-xs bg-[#FFFFFF]">
            <button
              onClick={handlePrevPage}
              className="relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 hover:bg-gray-50 focus:z-20 focus:outline-offset-0"
            >
              <span className="sr-only">Previous</span>
              <ChevronLeft aria-hidden="true" className="size-5" color="gray" />
            </button>

            {getPageNumbers().map((page, index) => {
              if (page === '...') {
                return (
                  <span key={index} className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900">
                    ...
                  </span>
                );
              }

              const isActivePage = page === currentPage();
              return (
                <button
                  onClick={() => handlePageChange(page)}
                  className={`relative inline-flex items-center px-4 py-2 text-sm font-semibold ${isActivePage ? 'bg-[#181818] text-white' : 'text-gray-900 ring-1 ring-[#F2F2F4] ring-inset hover:bg-gray-50'} focus:z-20 focus:outline-offset-0`}
                  key={page}
                >
                  {page}
                </button>
              );
            })}

            <button
              onClick={handleNextPage}
              className="relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-[#F2F2F4] ring-inset hover:bg-gray-50 focus:z-20 focus:outline-offset-0"
            >
              <span className="sr-only">Next</span>
              <ChevronRight aria-hidden="true" className="size-5" color="gray" />
            </button>
          </nav>
        </div>
      </div>
    </div>
  );
};
