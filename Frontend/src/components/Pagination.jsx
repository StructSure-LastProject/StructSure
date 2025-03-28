import { useSearchParams } from '@solidjs/router';
import { ChevronLeft, ChevronRight } from 'lucide-solid';
import { createEffect, createSignal } from 'solid-js';

/**
 * Pagination
 * @param {Number} limit The page limit
 * @param {Number} offset The offset
 * @param {Number} totalItems The total number of sensors
 * @param {Function} setOffset The setter for the offset
 * @returns The pagination component
 */
export const Pagination = ({ limit, offset, totalItems, setOffset}) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [currentPage, setCurrentPage] = createSignal(Math.floor(offset() / limit()) + 1);
  const [pages, setPages] = createSignal([]);
  const [totalPages, setTotalPages] = createSignal(0);

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
      const val = offset() - limit();
      setOffset(val);
      setSearchParams({ offset: val });
      updateCurrentPage(); 
    }
  };

  /**
   * Go to next page
   */
  const handleNextPage = () => {
    if (offset() + limit() < totalItems()) {
      const val = offset() + limit();
      setOffset(val);
      setSearchParams({ offset: val });
      updateCurrentPage();
    }
  };

  /**
   * Handle page change
   * @param {Number} pageNumber 
   */
  const handlePageChange = (pageNumber) => {
    const val = (pageNumber - 1) * limit();
    setOffset(val);
    setSearchParams({ offset: val });
    setCurrentPage(pageNumber); 
  };


/**
 * Calculate the page numbers
 * @returns The pages numbers
 */
const getPageNumbers = () => {
  const pagesArray = [];
  if (totalPages() <= 7) {
    for (let i = 1; i <= totalPages(); i++) {
      pagesArray.push(i);
    }
  } 
  else {
    pagesArray.push(1);

    if (currentPage() > 3) pagesArray.push('...');

    for (let i = Math.max(currentPage() - 1, 2); i <= Math.min(currentPage() + 1, totalPages() - 1); i++) {
      pagesArray.push(i);
    }

    if (currentPage() < totalPages() - 2) pagesArray.push('...');

    if (totalPages() > 1) pagesArray.push(totalPages());
  }    
  return pagesArray;
};


/**
 * Create effect to update
 */
createEffect(() => {
  setTotalPages(Math.ceil(totalItems() / limit()));
  setPages(getPageNumbers());
  setCurrentPage(pages().includes(currentPage()) ? currentPage() : 1);
  setCurrentPage(currentPage()); 
});


  return (
    <div className="flex items-center justify-between py-5">
      <div className="flex flex-1 gap-[10px] justify-between sm:hidden">
        <button
          onClick={handlePrevPage}
          className="relative inline-flex items-center rounded-[50px] bg-white px-[16px] py-[8px] accent"
        >
          Précédent
        </button>
        <button
          onClick={handleNextPage}
          className="relative inline-flex items-center rounded-[50px] bg-white px-[16px] py-[8px] accent"
        >
          Suivant
        </button>
      </div>
      <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
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
              <span className="sr-only">Précédent</span>
              <ChevronLeft aria-hidden="true" className="size-5" color="gray" />
            </button>

            {pages().map((page, index) => {
              if (page === '...') {
                return (
                  <span key={index} className="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-900">
                    ...
                  </span>
                );
              }
              return (
                <button
                  onClick={() => handlePageChange(page)}
                  className={`relative inline-flex items-center px-4 py-2 text-sm font-semibold ${page === currentPage() ? 'bg-[#181818] text-white' : 'text-gray-900 ring-1 ring-[#F2F2F4] ring-inset hover:bg-gray-50'} focus:z-20 focus:outline-offset-0`}
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
              <span className="sr-only">Suivant</span>
              <ChevronRight aria-hidden="true" className="size-5" color="gray" />
            </button>
          </nav>
        </div>
      </div>
    </div>
  );
};
