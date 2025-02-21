/**
 * Test is the variable contains only letters
 * @param {string} str 
 * @returns 
 */
export const containsNonLetters = (str) => /^[A-Za-zÀ-ÿ -]*$/.test(str);


/**
 * Validate the login value A-z, A-Z, 0-9, _, @
 * @param {string} login 
 * @returns 
 */
export const loginValidator = (loginValue) => /^[A-Za-z0-9_@.-]*$/.test(loginValue);


/**
 * Validate User Account Form
 * @param {string} firstName 
 * @param {string} lastName 
 * @param {string} login 
 * @param {string} role 
 * @param {string} password 
 * @param {Function} addError 
 * @param {Function} removeError 
 */
export const validateUserAccountForm = (firstName, lastName, login, role, password, addError, removeError, isPasswordRequired) => {
    
    const fields = [
        lastName,
        firstName,
        login,
        role,
    ];

    if (isPasswordRequired) {
        fields.push(password);
    }

    
    const missingFields = Object.values(fields).map(item => item === "" ? 1 : 0).filter(field => field !== 0);
    const errorMessage = "Assurez-vous que tous les champs marqués d'un astérisque (*) sont complétés.";
    const passworErrorMessage = "Le champ mot de passe doit contenir entre 12 et 64 caractères.";
    const firstnameError = "Le prénom doit contenir uniquement des lettres.";
    const lastnameError = "Le nom doit contenir uniquement des lettres.";
    const loginError = "Le champ identifiant doit contenir uniquement des lettres, des chiffres, des underscores et des @.";
    
    if (missingFields.length > 0) {
        addError(errorMessage)
    }
    else {
        removeError(errorMessage)
    }
    
    if (password.length !== 0 && password.length < 12) {
        addError(passworErrorMessage)
    }
    else {
        removeError(passworErrorMessage)
    }
    
    if (!containsNonLetters(firstName)) {
        addError(firstnameError)
    }
    else {
        removeError(firstnameError);
    }
    
    if (!containsNonLetters(lastName)) {
        addError(lastnameError)
    }
    else {
        removeError(lastnameError);
    }


    if(!loginValidator(login)){
        addError(loginError);
    }
    else {
        removeError(loginError);
    }
}