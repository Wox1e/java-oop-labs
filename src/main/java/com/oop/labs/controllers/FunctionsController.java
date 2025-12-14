package com.oop.labs.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oop.labs.entities.FunctionDTO;
import com.oop.labs.entities.functionEntity;
import com.oop.labs.entities.userEntity;
import com.oop.labs.services.FunctionService;
import com.oop.labs.services.PointService;
import com.oop.labs.services.UserService;
import com.oop.labs.utils.PolynomialApproximation;
import com.oop.labs.entities.pointEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/functions")
public class FunctionsController{

    private static final Logger logger = LogManager.getLogger(FunctionsController.class);
    private final FunctionService service;
    private final UserService userService;
    private final PointService pointService;

    FunctionsController(FunctionService service, UserService userService, PointService pointService) {
        this.service = service;
        this.userService = userService;
        this.pointService = pointService;
    }

    @PostMapping("/")
    public ResponseEntity<?> save(Authentication authentication, @Valid @RequestBody FunctionDTO functionDTO) {
        String username = authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Определяем режим сохранения (по умолчанию polynomial)
            String storageMode = functionDTO.getStorageMode();
            if (storageMode == null || storageMode.isEmpty()) {
                storageMode = "polynomial";
            }

            if (functionDTO.getPoints() == null || functionDTO.getPoints().size() < 2) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Функция должна содержать минимум 2 точки",
                        "timestamp", System.currentTimeMillis()
                ));
            }

            // Создаем функцию
            functionEntity function = new functionEntity();
            function.setName(functionDTO.getName());
            function.setType(functionDTO.getType());
            function.setAuthor_id(user.get().getId());

            if ("pointwise".equals(storageMode)) {
                // Сохраняем поточечно
                function.setPolynomialCoefficients(null);
                function.setXMin(null);
                function.setXMax(null);
                function.setOriginalPointCount(null);
                
                functionEntity saved = service.saveFunction(function);
                
                // Сохраняем точки отдельно
                for (FunctionDTO.PointDTO pointDTO : functionDTO.getPoints()) {
                    pointEntity point = new pointEntity();
                    point.setFunction_id(saved.getId());
                    point.setX_value(pointDTO.getX());
                    point.setY_value(pointDTO.getY());
                    pointService.savePoint(point);
                }
                
                logger.info("Сохраняем функцию {} поточечно для пользователя {}", saved.getName(), username);
                
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "status", "success",
                        "created", true,
                        "id", saved.getId(),
                        "timestamp", System.currentTimeMillis()
                ));
            } else {
                // Сохраняем через полиномиальную аппроксимацию
                List<PolynomialApproximation.Point> points = new ArrayList<>();
                for (FunctionDTO.PointDTO pointDTO : functionDTO.getPoints()) {
                    points.add(new PolynomialApproximation.Point(pointDTO.getX(), pointDTO.getY()));
                }

                // Аппроксимируем точки полиномом
                double[] coefficients = PolynomialApproximation.approximate(points);
                
                // Находим диапазон x
                double xMin = points.stream().mapToDouble(p -> p.x).min().orElse(0.0);
                double xMax = points.stream().mapToDouble(p -> p.x).max().orElse(1.0);

                function.setPolynomialCoefficients(PolynomialApproximation.coefficientsToJson(coefficients));
                function.setXMin(xMin);
                function.setXMax(xMax);
                function.setOriginalPointCount(points.size());

                functionEntity saved = service.saveFunction(function);
                logger.info("Сохраняем функцию {} в полиномиальной форме для пользователя {}", 
                        saved.getName(), username);
                
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "status", "success",
                        "created", true,
                        "id", saved.getId(),
                        "timestamp", System.currentTimeMillis()
                ));
            }
        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации коэффициентов полинома", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Ошибка обработки функции",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при сохранении функции", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage() != null ? e.getMessage() : "Ошибка при сохранении функции",
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> get(Authentication authentication,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String name) {
        logger.info("auth: {}", authentication.getName());
        String username =  authentication.getName();
        Optional<userEntity> user = userService.findUsersByUsername(username);


        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UUID authorId = user.get().getId();

        logger.info("Ищем функцию - authorId: {} | type: {} | name: {}", authorId, type, name);

        try {
            List<functionEntity> functions = service.findFiltered(authorId, type, name);
            
            // Конвертируем Entity в DTO, восстанавливая точки из полинома
            List<FunctionDTO> functionDTOs = new ArrayList<>();
            for (functionEntity func : functions) {
                FunctionDTO dto = convertToDTO(func);
                functionDTOs.add(dto);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", functions != null && !functions.isEmpty(),
                    "data", functionDTOs,
                    "count", functions != null ? functions.size() : 0,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при поиске функций", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "found", false,
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }


    @GetMapping("/{function_id}")
    public ResponseEntity<?> getByID(Authentication authentication, @PathVariable UUID function_id) {
        logger.info("Ищем функцию по id {}", function_id);

        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<functionEntity> function = service.findFunctionById(function_id);

            if (function.isEmpty() || !user.get().getId().equals(function.get().getAuthor_id())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of(
                                "status", "error",
                                "found", false,
                                "message", "Function not found with id: " + function_id,
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            // Конвертируем Entity в DTO, восстанавливая точки из полинома
            FunctionDTO dto = convertToDTO(function.get());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "found", true,
                    "data", dto,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Ошибка при поиске функции по id: {}", function_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "found", false,
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    @DeleteMapping("/{function_id}")
    public ResponseEntity<?> deleteFunction(Authentication authentication, @PathVariable UUID function_id) {
        logger.info("Попытка удаления функции с id: {}", function_id);
        
        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UUID userId = user.get().getId();
            
            // Проверяем существование функции и принадлежность пользователю
            Optional<functionEntity> function = service.findFunctionById(function_id);
            
            if (function.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Function not found",
                                "timestamp", System.currentTimeMillis()
                        ));
            }
            
            if (!function.get().getAuthor_id().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", "error",
                                "message", "Access denied",
                                "timestamp", System.currentTimeMillis()
                        ));
            }
            
            // Удаляем функцию
            service.deleteFunctionById(function_id);
            
            logger.info("Функция с id {} удалена пользователем {}", function_id, username);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Function deleted successfully",
                    "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            logger.error("Ошибка при удалении функции с id: {}", function_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    @PutMapping("/{function_id}")
    public ResponseEntity<?> updateFunction(Authentication authentication,
                                            @PathVariable UUID function_id,
                                            @RequestBody Map<String, Object> payload) {
        logger.info("Попытка обновления функции с id: {}", function_id);

        try {
            String username = authentication.getName();
            Optional<userEntity> user = userService.findUsersByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<functionEntity> functionOpt = service.findFunctionById(function_id);
            if (functionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Function not found",
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            functionEntity function = functionOpt.get();
            if (!function.getAuthor_id().equals(user.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", "error",
                                "message", "Access denied",
                                "timestamp", System.currentTimeMillis()
                        ));
            }

            // Обновляем только разрешенные поля
            if (payload.containsKey("name")) {
                String name = Objects.toString(payload.get("name"), "").trim();
                if (name.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Name cannot be empty",
                            "timestamp", System.currentTimeMillis()
                    ));
                }
                function.setName(name);
            }

            if (payload.containsKey("type")) {
                String type = Objects.toString(payload.get("type"), "").trim();
                if (type.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Type cannot be empty",
                            "timestamp", System.currentTimeMillis()
                    ));
                }
                function.setType(type);
            }

            // Обновляем точки целиком, если они переданы
            if (payload.containsKey("points")) {
                try {
                    // Определяем режим сохранения
                    String storageMode = Objects.toString(payload.get("storageMode"), "polynomial");
                    
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> pointsData = (List<Map<String, Object>>) payload.get("points");
                    
                    if (pointsData.size() < 2) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "status", "error",
                                "message", "Функция должна содержать минимум 2 точки",
                                "timestamp", System.currentTimeMillis()
                        ));
                    }
                    
                    if ("pointwise".equals(storageMode)) {
                        // Удаляем старые точки
                        List<pointEntity> oldPoints = pointService.findPointsByFunctionId(function_id);
                        for (pointEntity point : oldPoints) {
                            pointService.deletePointById(point.getId());
                        }
                        
                        // Сохраняем новые точки поточечно
                        for (Map<String, Object> pointData : pointsData) {
                            double x = ((Number) pointData.get("x")).doubleValue();
                            double y = ((Number) pointData.get("y")).doubleValue();
                            
                            pointEntity point = new pointEntity();
                            point.setFunction_id(function_id);
                            point.setX_value(x);
                            point.setY_value(y);
                            pointService.savePoint(point);
                        }
                        
                        // Очищаем полиномиальные данные
                        function.setPolynomialCoefficients(null);
                        function.setXMin(null);
                        function.setXMax(null);
                        function.setOriginalPointCount(null);
                    } else {
                        // Сохраняем через полиномиальную аппроксимацию
                        List<PolynomialApproximation.Point> points = new ArrayList<>();
                        for (Map<String, Object> pointData : pointsData) {
                            double x = ((Number) pointData.get("x")).doubleValue();
                            double y = ((Number) pointData.get("y")).doubleValue();
                            points.add(new PolynomialApproximation.Point(x, y));
                        }
                        
                        // Аппроксимируем точки полиномом
                        double[] coefficients = PolynomialApproximation.approximate(points);
                        
                        // Обновляем диапазон x
                        double xMin = points.stream().mapToDouble(p -> p.x).min().orElse(0.0);
                        double xMax = points.stream().mapToDouble(p -> p.x).max().orElse(1.0);
                        
                        function.setPolynomialCoefficients(PolynomialApproximation.coefficientsToJson(coefficients));
                        function.setXMin(xMin);
                        function.setXMax(xMax);
                        function.setOriginalPointCount(points.size());
                    }
                } catch (Exception e) {
                    logger.error("Ошибка обработки точек при обновлении", e);
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", "error",
                            "message", "Ошибка обработки точек: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
                }
            }

            functionEntity saved = service.saveFunction(function);
            logger.info("Функция {} обновлена пользователем {}", saved.getId(), username);

            // Возвращаем DTO с восстановленными точками
            FunctionDTO dto = convertToDTO(saved);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "updated", true,
                    "data", dto,
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            logger.error("Ошибка при обновлении функции с id: {}", function_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Internal server error",
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    /**
     * Конвертирует Entity в DTO, восстанавливая точки из полинома или загружая поточечно
     */
    private FunctionDTO convertToDTO(functionEntity entity) {
        FunctionDTO dto = new FunctionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setAuthorId(entity.getAuthor_id());
        
        try {
            // Проверяем, есть ли полиномиальные коэффициенты
            if (entity.getPolynomialCoefficients() != null && 
                entity.getXMin() != null && 
                entity.getXMax() != null &&
                entity.getOriginalPointCount() != null) {
                
                // Восстанавливаем точки из полинома
                double[] coefficients = PolynomialApproximation.jsonToCoefficients(
                        entity.getPolynomialCoefficients());
                
                int pointCount = entity.getOriginalPointCount();
                List<PolynomialApproximation.Point> restoredPoints = 
                        PolynomialApproximation.restorePoints(
                                coefficients, 
                                entity.getXMin(), 
                                entity.getXMax(), 
                                pointCount);
                
                // Конвертируем в DTO точки
                List<FunctionDTO.PointDTO> pointDTOs = new ArrayList<>();
                for (PolynomialApproximation.Point point : restoredPoints) {
                    pointDTOs.add(new FunctionDTO.PointDTO(point.x, point.y));
                }
                dto.setPoints(pointDTOs);
                dto.setStorageMode("polynomial");
            } else {
                // Загружаем точки поточечно из базы
                List<pointEntity> pointEntities = pointService.findPointsByFunctionId(entity.getId());
                List<FunctionDTO.PointDTO> pointDTOs = new ArrayList<>();
                for (pointEntity pointEntity : pointEntities) {
                    pointDTOs.add(new FunctionDTO.PointDTO(pointEntity.getX_value(), pointEntity.getY_value()));
                }
                dto.setPoints(pointDTOs);
                dto.setStorageMode("pointwise");
            }
        } catch (Exception e) {
            logger.error("Ошибка восстановления точек для функции {}", entity.getId(), e);
            dto.setPoints(new ArrayList<>());
        }
        
        return dto;
    }
}