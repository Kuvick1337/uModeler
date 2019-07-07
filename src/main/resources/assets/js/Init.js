// urlParams is null when used for embedding
window.urlParams = window.urlParams || {};

// Public global variables
window.MAX_REQUEST_SIZE = window.MAX_REQUEST_SIZE  || 10485760;
window.MAX_AREA = window.MAX_AREA || 15000 * 15000;

// URLs for save and export
window.EXPORT_URL = window.EXPORT_URL || 'http://localhost:8080/export';
window.SAVE_URL = window.SAVE_URL || 'http://localhost:8080/save';
window.OPEN_URL = window.OPEN_URL || 'http://localhost:8080/open';
window.RESOURCES_PATH = window.RESOURCES_PATH || 'assets/resources';
window.RESOURCE_BASE = window.RESOURCE_BASE || window.RESOURCES_PATH + '/grapheditor';
window.STENCIL_PATH = window.STENCIL_PATH || 'assets/stencils';
window.IMAGE_PATH = window.IMAGE_PATH || 'assets/images';
window.STYLE_PATH = window.STYLE_PATH || 'assets/styles';
window.CSS_PATH = window.CSS_PATH || 'assets/styles';
window.OPEN_FORM = window.OPEN_FORM || 'formOpen.html';

window.ULEARN_SAVE_URL = window.ULEARN_SAVE_URL || 'http://localhost:8080/ulearn/save';
window.ULEARN_LOAD_URL = window.ULEARN_LOAD_URL || 'http://localhost:8080/ulearn/load';

window.ULEARN_LOGIN_URL = window.ULEARN_LOGIN_URL || 'http://localhost:8080/ulearn/login';
window.ULEARN_FETCH_SUBMISSIONS_URL = 'http://localhost:8080/ulearn/submissions';
window.ULEARN_SAVE_DATA_URL = 'http://localhost:8080/ulearn/save';

// Sets the base path, the UI language via URL param and configures the
// supported languages to avoid 404s. The loading of all core language
// resources is disabled as all required resources are in grapheditor.
// properties. Note that in this example the loading of two resource
// files (the special bundle and the default bundle) is disabled to
// save a GET request. This requires that all resources be present in
// each properties file since only one file is loaded.
window.mxBasePath = window.mxBasePath || 'assets';
window.mxLanguage = window.mxLanguage || urlParams['lang'];
window.mxLanguages = window.mxLanguages || ['de'];
