'use strict';

let index = {
    init: function() {
        $("#btn-save").on("click", () => {
            this.save();
        });
    },

    save: function() {
        console.log("안녕");
    }
}
index.init();