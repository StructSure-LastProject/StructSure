import {createEffect, createSignal, Show} from "solid-js";
import {ChevronDown, ChevronRight, Dot, FolderSync, Pencil} from "lucide-solid";
import { useSearchParams } from "@solidjs/router";
import RestorePlanModal from "./RestorePlanModal.jsx"

/**
 * Component that displays a section with a toggle button
 * @param {Object} props Component properties
 * @param {string} props.name Section name to display
 * @param {Function} props.isOpen Signal indicating if the section is open
 * @param {Function} props.toggleOpen Function to toggle open/closed state
 * @returns {JSX.Element} Section component
 */
const Section = (props) => {
  return (
    <div
      class="bg-white py-[8px] px-[9px] flex items-center gap-x-[10px] rounded-[10px] cursor-pointer hover:bg-gray-50"
      onClick={props.toggleOpen}
      role="button"
    >
      <div class="w-4 h-4 flex items-center justify-center">
        {props.isOpen() ? <ChevronDown class="stroke-[2.5]" size={16} /> : <ChevronRight class="stroke-[2.5]" size={16} />}
      </div>
      <p class="subtitle leading-[11px]">{props.name}</p>
    </div>
  );
};

/**
 * Component that displays a plan without edit options
 * @param {Object} props Component properties
 * @param {string} props.name Plan name
 * @param {boolean} props.selectedPlanId contains the selected plan id
 * @returns {JSX.Element} Plan component
 */
const Plan = ({ name, selectedPlanId, setSelectedPlanId, planId, setSearchParams}) => (
  <div class={`px-[8px] py-[9px] rounded-[10px] cursor-pointer flex items-center gap-x-[10px] justify-between hover:bg-[#F2F2F4] ${selectedPlanId() === planId ? 'bg-[#F2F2F4]' : ''}`}
  onClick={() => {
    setSelectedPlanId(planId);
    setSearchParams({ selectedPlanId: planId });
  }}>
    <div class="flex items-center gap-x-[10px]">
      <div class="w-4 h-4 flex items-center justify-center">
        <Dot class="stroke-[5]"/>
      </div>
      <p class="poppins text-base font-medium leading-[11px]">{name}</p>
    </div>
  </div>
);


/**
 * Component that displays a plan with edit option
 * @param {Object} props Component properties
 * @param {string} props.name Plan name
 * @param {Function} props.onEdit Function called when clicking the edit button
 * @param {number|string} props.planId Plan identifier
 * @param {boolean} props.selectedPlanId contains the selected plan id
 * @returns {JSX.Element} PlanEdit component
 */
const PlanEdit = ({name, onEdit, planId, selectedPlanId, setSelectedPlanId, setSearchParams}) => (
  <div class={`py-[8px] px-[9px] rounded-[10px] flex items-center cursor-pointer gap-x-[10px] justify-between hover:bg-gray-100 group ${selectedPlanId() === planId ? 'bg-[#F2F2F4]' : ''}`}
  onClick={() => {
    setSelectedPlanId(planId);
    setSearchParams({ selectedPlanId: planId });
  }}>
    <div class="flex items-center gap-x-[10px]">
      <div class="w-4 h-4 flex items-center justify-center">
        <Dot class="stroke-[5]" size={16}/>
      </div>
      <p class="poppins text-base font-medium !leading-[11px]">{name}</p>
    </div>
    <div class="w-5 h-5 flex items-center justify-center invisible group-hover:visible">
      <button
        title="Éditer un plan"
        onClick={(e) => {
          e.stopPropagation();
          onEdit(planId);
        }}
      >
        <Pencil size={20} />
      </button>
    </div>
  </div>
);

/**
 * Component that displays an archived plan
 * @param {Object} props Component properties
 * @param {string} props.name Plan name
 * @param {number|string} props.structureId Structure identifier
 * @param {number|string} props.planId Plan identifier
 * @param {Function} props.onPlanEdit Function to be called after a plan is edited
 * @param {Function} props.onPlanRestore Function to be called after a plan is restores
 * @returns {JSX.Element} PlanArchived component
 */
const PlanArchived = (props) => {
  const [isRestoreModalOpen, setIsRestoreModalOpen] = createSignal(false);
  const [errorMsgRestorePlan, setErrorMsgRestorePlan] = createSignal("");

  /**
   * Open the restore modal
   * @param {Event} e
   */
  const handleArchivedClick = (e) => {
    e.stopPropagation();
    setIsRestoreModalOpen(true);
  };

  /**
   * Handle successful restoration
   * @param {Object} data - The data returned after restoration
   */
  const handleRestoreSuccess = (data) => {
    setIsRestoreModalOpen(false);
    if (props.onPlanRestore) {
      props.onPlanRestore(data?.id);
    }
  };

  return (
    <>
      <div class="py-[8px] px-[9px] rounded-[10px] flex items-center gap-x-[10px] justify-between">
        <div class="flex items-center gap-x-[10px]">
          <div class="w-4 h-4 flex items-center justify-center">
            <Dot class="stroke-[5]" size={16}/>
          </div>
          <p class="poppins text-base font-medium !leading-[11px] opacity-60">{props.name}</p>
        </div>
        <div class="w-5 h-5 flex items-center justify-center" title="Restaurer le plan">
          <button
            onClick={handleArchivedClick}
          >
            <FolderSync size={20} />
          </button>
        </div>
      </div>

      <Show when={isRestoreModalOpen()}>
        <RestorePlanModal
          planId={props.planId}
          structureId={props.structureId}
          onClose={() => {
            setIsRestoreModalOpen(false);
            setErrorMsgRestorePlan("");
          }}
          onRestore={handleRestoreSuccess}
          errorMsgRestorePlan={errorMsgRestorePlan}
          setErrorMsgRestorePlan={setErrorMsgRestorePlan}
        />
      </Show>
    </>
  );
};

/**
 * Builds a tree structure from plan data
 * @param {Array} data Array of plans with their information
 * @returns {Object} Object containing the tree structure of plans and sections
 * @property {Object} sections Sections with their child plans
 * @property {Array} rootPlans Plans without sections (at root level)
 */
const buildTree = (data) => {
  const tree = {
    sections: {},
    rootPlans: []
  };

  if (!Array.isArray(data)) {
    console.warn("Data is not an array:", data);
    return tree;
  }

  const sortedData = [...data].sort((a, b) => {
    const sectionA = a.section || "";
    const sectionB = b.section || "";
    return sectionA.localeCompare(sectionB);
  });

  sortedData.forEach((item) => {
    const { id, name, section, type } = item;

    if (!section || section.trim() === "") {
      tree.rootPlans.push({ name, id, type });
      return;
    }

    const sectionParts = section.split("/");
    let current = tree.sections;
    let currentPath = "";

    sectionParts.forEach((part, index) => {
      currentPath = currentPath ? `${currentPath}/${part}` : part;

      if (!current[part]) {
        current[part] = {
          name: part,
          children: {},
          type: "section",
          id: `section-${currentPath}`
        };
      }

      if (index === sectionParts.length - 1) {
        current[part].children[id] = { name, id, type };
      }

      current = current[part].children;
    });
  });

  return tree;
};

/**
 * Recursive component that displays a tree node (section or plan)
 * @param {Object} props Component properties
 * @param {string} props.name Node name
 * @param {string} props.type Node type ("section", "edit", "archived", "plan")
 * @param {Object} props.children Child nodes
 * @param {boolean} props.setSelectedPlanId Setter for plan Id
 * @param {Function} props.onEdit Function to edit a plan
 * @param {number|string} props.planId Plan identifier
 * @param {number|string} props.selectedPlanId Selected plan identifier
 * @param {number|string} props.structureId Structure identifier
 * @param {string} props.section Section path for the plan
 * @param {Function} props.onPlanEdit Function called after plan edit
 * @param {Function} props.onPlanRestore Function called after plan restore
 * @returns {JSX.Element} TreeNode component
 */
const TreeNode = (props) => {
  const [isOpen, setIsOpen] = createSignal(false);
  const hasChildren = Object.keys(props.children || {}).length > 0;

  /**
   * Change the toggle status
   * @param e Click event
   */
  const toggleOpen = (e) => {
    e.stopPropagation();
    setIsOpen(!isOpen());
  };


  if (props.type === "section") {
    return (
      <div class="mb-2">
        <Section
          name={props.name}
          isOpen={isOpen}
          toggleOpen={toggleOpen}
        />
        {hasChildren && isOpen() && (
          <div class="pl-4 mt-2">
            {Object.entries(props.children).map(([key, child]) => (
              <TreeNode
                name={child.name}
                type={child.type}
                children={child.children}
                setSelectedPlanId={props.setSelectedPlanId}
                selectedPlanId={props.selectedPlanId}
                onEdit={props.onEdit}
                planId={child.id}
                setSearchParams={props.setSearchParams}
                structureId={props.structureId}
                onPlanEdit={props.onPlanEdit}
                onPlanRestore={props.onPlanRestore}
              />
            ))}
          </div>
        )}
      </div>
    );
  }

  let Component;
  switch (props.type) {
    case "edit":
      Component = PlanEdit;
      break;
    case "archived":
      Component = PlanArchived;
      break;
    default:
      Component = Plan;
  }

  return (
    <div class="mb-2">
      <Component
        name={props.name}
        selectedPlanId={props.selectedPlanId}
        onEdit={props.onEdit}
        planId={props.planId}
        setSelectedPlanId={props.setSelectedPlanId}
        setSearchParams={props.setSearchParams}
        structureId={props.structureId}
        onPlanEdit={props.onPlanEdit}
        onPlanRestore={props.onPlanRestore}
      />
    </div>
  );
};

/**
 * Component that renders a plan based on its type
 * @param {Object} props Component properties
 * @param {Object} props.plan Plan data
 * @param {string} props.plan.type Plan type ("edit", "archived", "plan")
 * @param {string} props.plan.name Plan name
 * @param {number|string} props.plan.id Plan identifier
 * @param {number|string} props.selectedPlanId Selected plan identifier
 * @param {Function} props.onEdit Function to edit a plan
 * @param {number|string} props.structureId Structure identifier
 * @param {Function} props.onPlanEdit Function called after plan edit or restore
 * @returns {JSX.Element} Appropriate component based on plan type
 */
const RenderPlan = (props) => {
  let Component;
  switch (props.plan.type) {
    case "edit":
      Component = PlanEdit;
      break;
    case "archived":
      Component = PlanArchived;
      break;
    default:
      Component = Plan;
  }
  return (
    <Component
      name={props.plan.name}
      selectedPlanId={props.selectedPlanId}
      setSelectedPlanId={props.setSelectedPlanId}
      onEdit={props.onEdit}
      planId={props.plan.id}
      setSearchParams={props.setSearchParams}
      structureId={props.structureId}
      onPlanEdit={props.onPlanEdit}
    />
  );
};

/**
 * Main component that manages the display of the plan tree structure
 * @param {Object} props Component properties
 * @param {Array} props.data Plan data
 * @param {number|string} props.selectedPlanId Selected plan identifier
 * @param {Function} props.onEdit Function to edit a plan
 * @param {Function} props.onPlanEdit Function called after plan edit
 * @param {number|string} props.structureId Structure identifier
 * @param {Function} setSelectedPlanId Function to set the selected plan
 * @returns {JSX.Element} DropdownsSection component
 */
const DropdownsSection = (props) => {
  const [localPlans, setLocalPlans] = createSignal([]);

  const [searchParams, setSearchParams] = useSearchParams();

  const safeData = () => {
    if (Array.isArray(props.data)) {
      if (props.data.length > 0 && props.selectedPlanId() == null) {
        props.setSelectedPlanId(props.data[0].id);
        setSearchParams({ selectedPlanId: props.selectedPlanId() });
      }
    }
    return Array.isArray(props.data) ? props.data : [];
  };

  createEffect(() => {
    setLocalPlans(safeData());
  });

  /**
   * Build the plans data (section and plan)
   * @returns {{sections: {}, rootPlans: *[]}} Tree object
   */
  const treeData = () => {
    const currentPlans = localPlans();
    return buildTree(currentPlans);
  };

  return (
    <div class="space-y-2">
      {treeData().rootPlans.map(plan => (
        <RenderPlan
          plan={plan}
          selectedPlanId={props.selectedPlanId}
          onEdit={props.onEdit}
          setSelectedPlanId={props.setSelectedPlanId}
          setSearchParams={setSearchParams}
          structureId={props.structureId}
          onPlanEdit={props.onPlanEdit}
          onPlanRestore={props.onPlanRestore}
        />
      ))}

      {Object.entries(treeData().sections).map(([path, section]) => (
        <TreeNode
          name={section.name}
          type={section.type}
          children={section.children}
          selectedPlanId={props.selectedPlanId}
          setSelectedPlanId={props.setSelectedPlanId}
          onEdit={props.onEdit}
          setSearchParams={setSearchParams}
          structureId={props.structureId}
          onPlanEdit={props.onPlanEdit}
          onPlanRestore={props.onPlanRestore}
        />
      ))}
    </div>
  );
};

export default DropdownsSection;