import {createEffect, createSignal} from "solid-js";
import { ChevronDown, ChevronRight, Pencil, FolderSync, Dot } from "lucide-solid";
import ModalEditPlan from "../Plan/ModalEditPlan";

const buildTree = (data) => {
  //delete
  console.log("Building tree with data:", data); // Debug log

  const tree = {
    sections: {},
    rootPlans: []
  };

  //delete
  if (!data || !Array.isArray(data)) {
    console.log("Data is not an array:", data);
    return tree;
  }

  data.forEach(({ id, name, section, type }) => {
    if (!section || section === "") {
      tree.rootPlans.push({ name, id, type });
      return;
    }

    const path = section.split("/");
    let current = tree.sections;
    path.forEach((part, index) => {
      if (!current[part]) {
        current[part] = { name: part, children: {}, type: "section", id: `section-${part}` };
      }
      if (index === path.length - 1) {
        current[part].children[id] = { name, id, type, children: {} };
      }
      current = current[part].children;
    });
  });

  return tree;
};

const Section = (props) => {
  return (
    <div
      class="bg-white px-[8px] py-[9px] flex items-center gap-x-[10px] rounded-[10px] cursor-pointer hover:bg-gray-50"
      onClick={props.toggleOpen}
    >
      <div class="w-4 h-4 flex items-center justify-center">
        {props.isOpen() ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
      </div>
      <p class="prose font-poppins poppins text-base font-semibold leading-none">{props.name}</p>
    </div>
  );
};

const Plan = ({ name, isSelected }) => (
  <div class={`px-[8px] py-[9px] rounded-[10px] flex items-center gap-x-[10px] justify-between hover:bg-[#F2F2F4] ${isSelected ? 'bg-[#F2F2F4]' : ''}`}>
    <div class="flex items-center gap-x-[10px]">
      <div class="w-4 h-4 flex items-center justify-center">
        <Dot/>
      </div>
      <p class="prose font-poppins poppins text-base font-semibold leading-none">{name}</p>
    </div>
  </div>
);

const PlanEdit = ({name, onEdit, planId}) => (
  <div class="px-[8px] py-[9px] rounded-[10px] flex items-center gap-x-[10px] justify-between hover:bg-gray-100">
    <div class="flex items-center gap-x-[10px]">
      <div class="w-4 h-4 flex items-center justify-center">
        <Dot size={16}/>
      </div>
      <p class="prose font-poppins poppins text-base font-semibold leading-none">{name}</p>
    </div>
    <div class="w-5 h-5 flex items-center justify-center">
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

const PlanArchived = ({ name }) => (
  <div class="px-[8px] py-[9px] rounded-[10px] flex items-center gap-x-[10px] justify-between">
    <div class="flex items-center gap-x-[10px]">
      <div class="w-4 h-4 flex items-center justify-center">
        <Dot size={16}/>
      </div>
      <p class="prose italic font-poppins poppins text-base leading-none">{name}</p>
    </div>
    <div class="w-5 h-5 flex items-center justify-center">
      <FolderSync size={20} />
    </div>
  </div>
);

const TreeNode = (props) => {
  const [isOpen, setIsOpen] = createSignal(false);
  const hasChildren = Object.keys(props.children || {}).length > 0;

  //delete
  console.log("TreeNode rendering with:", { // Debug log
    name: props.name,
    type: props.type,
    hasChildren,
    children: props.children
  });

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
                key={key}
                name={child.name}
                type={child.type}
                children={child.children}
                isSelected={props.selectedPlanId === child.id}
                onEdit={props.onEdit}
                planId={child.id}
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
        isSelected={props.isSelected}
        onEdit={props.onEdit}
        planId={props.planId}
      />
    </div>
  );
};

const RenderPlan = (plan, selectedPlanId, onEdit) => {
  let Component;
  switch (plan.type) {
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
      key={plan.id}
      name={plan.name}
      isSelected={selectedPlanId === plan.id}
      onEdit={onEdit}
      planId={plan.id}
    />
  );
};

const DropdownsSection = (props) => {
  const [localPlans, setLocalPlans] = createSignal(props.plans || []);
  const [selectedPlanId, setSelectedPlanId] = createSignal(props.selectedPlanId || null);
  const [isEditModalOpen, setIsEditModalOpen] = createSignal(false);
  const [selectedPlan, setSelectedPlan] = createSignal(null);

  createEffect(() => {
    if (props.data) {
      setLocalPlans(props.data);
    }
  });

  createEffect(() => {
    setSelectedPlanId(props.selectedPlanId);
  });

  const getImageUrl = (planId) => {
    const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/';
    return `${API_BASE_URL}/api/structures/plans/${planId}/image`;
  };

  const handleEdit = (planId) => {
    const plan = localPlans().find(p => p.id === planId);
    if (plan) {
      setSelectedPlan({
        ...plan,
        imageUrl: getImageUrl(plan.id)
      });
      setIsEditModalOpen(true);
    }
  };

  const handleSavePlan = (updatedPlan) => {
    if (props.onPlanEdit) {
      props.onPlanEdit(updatedPlan);
    }
    closeEditModal();
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setSelectedPlan(null);
  };

  // delete
  console.log("DropdownsSection receiving data:", props.data); // Debug log

  const { sections, rootPlans } = buildTree(localPlans());

  // delete
  console.log("Processing tree:", { sections, rootPlans }); // Debug log

  return (
    <div class="space-y-2">
      {rootPlans.map((plan) => (
        <div class="mb-2">
          <RenderPlan
            plan={plan}
            selectedPlanId={selectedPlanId()}
            onEdit={handleEdit}
          />
        </div>
      ))}

      {Object.entries(sections).map(([key, section]) => (
        <TreeNode
          key={key}
          name={section.name}
          type={section.type}
          children={section.children}
          selectedPlanId={selectedPlanId()}
          onEdit={handleEdit}
        />
      ))}

      {isEditModalOpen() && selectedPlan() && (
        <ModalEditPlan
          isOpen={isEditModalOpen()}
          onSave={handleSavePlan}
          onClose={closeEditModal}
          structureId={props.structureId}
          plan={selectedPlan()}
        />
      )}
    </div>
  );
};

export default DropdownsSection;