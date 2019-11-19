(function() {
    // Functions
    // =========================================================================
    /**
     * Adds event listeners to change active stylesheet and restore previously
     * activated stylesheet on reload.
     *
     * @example
     *
     * This link:
     *   <a href="#" data-link-title="Foo">Foo</a>
     * Will active this existing link:
     *   <link rel="stylesheet alternate" title="Foo" href="..." >
     *
     * @example
     *
     * This link:
     *   <a href="#" data-link-href="path/to/file.css">Bar</a>
     * Will activate this existing link:
     *   <link rel="stylesheet alternate" title="[someID]" href="path/to/file.css" >
     * Or generate this active link:
     *   <link rel="stylesheet" title="Bar" href="path/to/file.css" >
     */
    function initStyleSwitcher() {
        var isInitialzed      = false;
        var sessionStorageKey = 'activeStylesheetHref';

        function handleSwitch(activeHref, activeTitle) {
            var activeElm = document.querySelector('link[href*="' + activeHref +'"],link[title="' + activeTitle +'"]');

            if (!activeElm && activeHref) {
                activeElm = document.createElement('link');
                activeElm.setAttribute('href', activeHref);
                activeElm.setAttribute('rel', 'stylesheet');
                activeElm.setAttribute('title', activeTitle);

                document.head.appendChild(activeElm);

                activeElm.addEventListener('load', function linkOnLoad() {
                    activeElm.removeEventListener('load', linkOnLoad);
                    setActiveLink(activeElm);
                });
            }
            else if (activeElm) {
                setActiveLink(activeElm);
            }
        }

        function setActiveLink(activeElm) {
            var activeHref   = activeElm.getAttribute('href');
            var activeTitle  = activeElm.getAttribute('title');
            var inactiveElms = document.querySelectorAll('link[title]:not([href*="' + activeHref +'"]):not([title="' + activeTitle +'"])');

            // Remove "alternate" keyword
            activeElm.setAttribute('rel', (activeElm.rel || '').replace(/\s*alternate/g, '').trim());

            // Force enable stylesheet (required for some browsers)
            activeElm.disabled = true;
            activeElm.disabled = false;

            // Store active style sheet
            sessionStorage.setItem(sessionStorageKey, activeHref);

            // Disable other elms
            for (var i = 0; i < inactiveElms.length; i++) {
                var elm = inactiveElms[i];

                elm.disabled = true;

                // Fix for browsersync and alternate stylesheet updates. Will
                // cause FOUC when switching stylesheets during development, but
                // required to properly apply style updates when alternate
                // stylesheets are enabled.
                if (window.browsersyncObserver) {
                    var linkRel    = elm.getAttribute('rel') || '';
                    var linkRelAlt = linkRel.indexOf('alternate') > -1 ? linkRel : (linkRel + ' alternate').trim();

                    elm.setAttribute('rel', linkRelAlt);
                }
            }

            // CSS custom property ponyfil
            if ((window.$docsify || {}).themeable) {
                window.$docsify.themeable.util.cssVars();
            }
        }

        // Event listeners
        if (!isInitialzed) {
            isInitialzed = true;

            // Restore active stylesheet
            document.addEventListener('DOMContentLoaded', function() {
                var activeHref = sessionStorage.getItem(sessionStorageKey);

                if (activeHref) {
                    handleSwitch(activeHref);
                }
            });

            // Update active stylesheet
            document.addEventListener('click', function(evt) {
                var dataHref  = evt.target.getAttribute('data-link-href');
                var dataTitle = evt.target.getAttribute('data-link-title')

                if (dataHref || dataTitle) {
                    dataTitle = dataTitle
                        || evt.target.textContent
                        || '_' + Math.random().toString(36).substr(2, 9); // UID

                    handleSwitch(dataHref, dataTitle);
                    evt.preventDefault();
                }
            });
        }
    }

    // Main
    // =========================================================================
    initStyleSwitcher();
})();
