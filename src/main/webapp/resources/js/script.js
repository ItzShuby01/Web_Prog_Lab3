"use strict";

// Variables populated by the script inside main.xhtml
window.currentR = 3.0;
window.currentPoints = [];

// JSF ID Constants matching the  main.xhtml form IDs
const FORM_ID = 'form';
const ID_CANVAS_X = FORM_ID + ':canvas_x';
const ID_CANVAS_Y = FORM_ID + ':canvas_y';
const ID_CANVAS_R = FORM_ID + ':canvas_r';
const ID_IS_CANVAS = FORM_ID + ':is_canvas_submit';


// Main function to draw the graph and bind events.
function initGraph() {
    const canvas = document.getElementById('graph-canvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const width = canvas.width;   // Internal resolution
    const height = canvas.height; // Internal resolution
    const unit = width / 10;      // Scale unit

    // CLICK EVENT HANDLER (Re-bound on every init)
    canvas.onclick = function(event) {
        // Get R from global variable or fallback to radio
        let rVal = window.currentR;

        // Basic validation
        if (!rVal || rVal < 1 || rVal > 5) {
            const radio = document.querySelector('input[name*="r_radio_group"]:checked');
            rVal = radio ? parseFloat(radio.value) : 0;
            if(rVal < 1 || rVal > 5) {
                alert("Please select a valid Radius (R) first.");
                return;
            }
        }

        const rect = canvas.getBoundingClientRect(); // Visual dimensions

        // Calculate scale factors (Internal Resolution / Visual Size)
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;

        // Get the click position relative to element
        const clickX = (event.clientX - rect.left) * scaleX;
        const clickY = (event.clientY - rect.top) * scaleY;

        const centerX = width / 2;
        const centerY = height / 2;

        // Convert to Graph Coordinates
        const valX = (clickX - centerX) / unit;
        const valY = (centerY - clickY) / unit;

        // Update Hidden Inputs
        document.getElementById(ID_CANVAS_X).value = valX.toFixed(6);
        document.getElementById(ID_CANVAS_Y).value = valY.toFixed(6);
        document.getElementById(ID_CANVAS_R).value = rVal;

        // Set flag for Bean
        document.getElementById(ID_IS_CANVAS).value = "true";

        // Call PrimeFaces RemoteCommand
        if (typeof processCanvasClick === 'function') {
            processCanvasClick();
        } else {
            console.error("RemoteCommand 'processCanvasClick' is not defined.");
        }
    };



    // DRAWING LOGIC

    const toPxX = (val) => width / 2 + val * unit;
    const toPxY = (val) => height / 2 - val * unit;

    // Clear
    ctx.clearRect(0, 0, width, height);

    // Dynamic R
    const r = window.currentR;
    const rPx = r * unit;
    const cx = width / 2;
    const cy = height / 2;

    // Styles
    ctx.fillStyle = "rgba(51, 153, 255, 0.5)"; // Blue fill
    ctx.strokeStyle = "#3399FF";
    ctx.lineWidth = 2;

    // A) Triangle (Quadrant 1)
    ctx.beginPath();
    ctx.moveTo(toPxX(0), toPxY(0));
    ctx.lineTo(toPxX(r/2), toPxY(0));
    ctx.lineTo(toPxX(0), toPxY(r));
    ctx.closePath();
    ctx.fill(); ctx.stroke();

    // B) Rectangle (Quadrant 3)
    ctx.beginPath();
    ctx.rect(toPxX(-r/2), toPxY(0), (r/2)*unit, r*unit);
    ctx.fill(); ctx.stroke();

    // C) Semicircle (Quadrant 4)
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.arc(cx, cy, rPx, Math.PI / 2, 0, true);
    ctx.closePath();
    ctx.fill(); ctx.stroke();

    // AXES & GRID
    ctx.strokeStyle = '#fff';
    ctx.lineWidth = 1;

    // Axes
    ctx.beginPath();
    ctx.moveTo(0, cy); ctx.lineTo(width, cy); // X
    ctx.moveTo(cx, 0); ctx.lineTo(cx, height); // Y
    ctx.stroke();

    // Ticks & Labels
    ctx.fillStyle = '#fff';
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';

    const labels = [-5, -4, -3, -2, -1, 1, 2, 3, 4, 5];
    labels.forEach(i => {
        const x = toPxX(i);
        const y = toPxY(i);

        // X-Axis ticks
        ctx.beginPath();
        ctx.moveTo(x, cy - 3); ctx.lineTo(x, cy + 3);
        ctx.stroke();
        ctx.fillText(i, x, cy + 15);

        // Y-Axis ticks
        ctx.beginPath();
        ctx.moveTo(cx - 3, y); ctx.lineTo(cx + 3, y);
        ctx.stroke();
        ctx.fillText(i, cx - 15, y + 4);
    });

    ctx.fillText("X", width - 10, cy - 10);
    ctx.fillText("Y", cx + 10, 10);

    // DRAW POINTS
    if (window.currentPoints && Array.isArray(window.currentPoints)) {
        window.currentPoints.forEach(p => {
            const px = toPxX(p.x);
            const py = toPxY(p.y);

            ctx.beginPath();
            ctx.fillStyle = p.hit ? "#4ade80" : "#ef4444";
            ctx.arc(px, py, 4, 0, 2 * Math.PI);
            ctx.fill();

            ctx.strokeStyle = "#fff";
            ctx.lineWidth = 1;
            ctx.stroke();
        });
    }
}